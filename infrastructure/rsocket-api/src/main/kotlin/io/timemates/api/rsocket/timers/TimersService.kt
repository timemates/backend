package io.timemates.api.rsocket.timers

import com.google.protobuf.Empty
import io.timemates.api.rsocket.internal.*
import io.timemates.api.rsocket.users.rs
import io.timemates.api.timers.members.invites.requests.GetInvitesRequest
import io.timemates.api.timers.members.invites.requests.InviteMemberRequest
import io.timemates.api.timers.members.invites.requests.RemoveInviteRequest
import io.timemates.api.timers.members.requests.GetMembersRequest
import io.timemates.api.timers.members.requests.KickMemberRequest
import io.timemates.api.timers.requests.*
import io.timemates.api.timers.types.Timer
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerSettings
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerDescription
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.types.value.TimerName
import io.timemates.backend.timers.usecases.*
import io.timemates.backend.timers.usecases.members.GetMembersUseCase
import io.timemates.backend.timers.usecases.members.KickTimerUserUseCase
import io.timemates.backend.timers.usecases.members.invites.CreateInviteUseCase
import io.timemates.backend.timers.usecases.members.invites.GetInvitesUseCase
import io.timemates.backend.timers.usecases.members.invites.RemoveInviteUseCase
import io.timemates.backend.users.types.value.UserId
import kotlin.time.Duration.Companion.milliseconds
import io.timemates.api.timers.TimersService as RSTimersService

/**
 * A service class that provides various timer-related functionality.
 *
 * This class extends the AbstractTimersService class and implements all the required methods.
 * It takes several use cases as dependencies, such as createInviteUseCase, createTimerUseCase, etc.
 * These use cases provide the business logic for performing specific actions related to timers.
 */
class TimersService(
    private val createInviteUseCase: CreateInviteUseCase,
    private val createTimerUseCase: CreateTimerUseCase,
    private val removeTimerUseCase: RemoveTimerUseCase,
    private val setTimerInfoUseCase: SetTimerInfoUseCase,
    private val getInvitesUseCase: GetInvitesUseCase,
    private val getMembersUseCase: GetMembersUseCase,
    private val getTimersUseCase: GetTimersUseCase,
    private val kickTimerUserUseCase: KickTimerUserUseCase,
    private val removeInviteUseCase: RemoveInviteUseCase,
    private val getTimerUseCase: GetTimerUseCase,
) : RSTimersService() {
    /**
     * Creates a timer with the provided request parameters.
     *
     * @param request The request object that contains the name, description, and settings of the timer.
     * @return The response object containing the ID of the created timer.
     */
    override suspend fun createTimer(
        request: CreateTimerRequest,
    ): CreateTimerRequest.Response = authorized {
        val result = createTimerUseCase.execute(
            name = TimerName.createOrFail(request.name),
            description = TimerDescription.createOrFail(request.description),
            settings = request.settings?.core() ?: TimerSettings.Default,
        )

        when (result) {
            is CreateTimerUseCase.Result.Success -> CreateTimerRequest.Response.create {
                timerId = result.timerId.long
            }

            CreateTimerUseCase.Result.TooManyCreations -> tooManyRequests()
        }
    }

    /**
     * Retrieves a timer based on the provided request.
     *
     * @param request The request object containing the timer ID.
     * @return The retrieved timer.
     */
    override suspend fun getTimer(
        request: GetTimerRequest,
    ): Timer = authorized {
        val result = getTimerUseCase.execute(TimerId.createOrFail(request.timerId))

        when (result) {
            is GetTimerUseCase.Result.Success -> result.timer.rs()
            GetTimerUseCase.Result.NotFound -> notFound()
        }
    }

    /**
     * Retrieves a list of user's timers based on the given request with pagination ability.
     *
     * @param request The request object containing the nextPageToken.
     * @return The response object containing the list of timers and the nextPageToken.
     */
    override suspend fun getTimers(
        request: GetTimersRequest,
    ): GetTimersRequest.Response = authorized {
        val result = getTimersUseCase.execute(PageToken.accept(request.nextPageToken))

        when (result) {
            is GetTimersUseCase.Result.Success -> GetTimersRequest.Response.create {
                timers = result.page.value.map { it.rs() }
                nextPageToken = result.page.nextPageToken?.forPublic().orEmpty()
            }
        }
    }

    /**
     * Edits the timer with the given request.
     *
     * @param request The request containing the necessary information to edit the timer.
     * @return An instance of [Empty] indicating the result of the operation.
     */
    override suspend fun editTimer(
        request: EditTimerRequest,
    ): Empty = authorized {
        val result = setTimerInfoUseCase.execute(
            timerId = TimerId.createOrFail(request.timerId),
            patch = TimersRepository.TimerInformation.Patch(
                name = request.name.nullIfEmpty()?.let { TimerName.createOrFail(it) },
                description = TimerDescription.createOrFail(request.description),
            ),
            newSettings = TimerSettings.Patch(
                workTime = request.settings?.workTime?.takeIf { it > 0 }?.milliseconds,
                restTime = request.settings?.restTime?.takeIf { it > 0 }?.milliseconds,
                bigRestEnabled = request.settings?.bigRestEnabled,
                bigRestTime = request.settings?.bigRestPer?.takeIf { it > 0 }?.milliseconds,
                bigRestPer = request.settings?.bigRestPer?.takeIf { it > 0 }?.let { Count.createOrFail(it) },
            ),
        )

        when (result) {
            SetTimerInfoUseCase.Result.NoAccess -> noAccess()
            SetTimerInfoUseCase.Result.NotFound -> notFound()
            SetTimerInfoUseCase.Result.Success -> Empty.Default
        }
    }

    /**
     * Kicks a member from a timer.
     *
     * @param request The request containing the timer ID and user ID.
     * @return An instance of [Empty].
     */
    override suspend fun kickMember(request: KickMemberRequest): Empty = authorized {
        val result = kickTimerUserUseCase.execute(
            TimerId.createOrFail(request.timerId),
            UserId.createOrFail(request.userId),
        )

        when (result) {
            KickTimerUserUseCase.Result.NoAccess -> noAccess()
            KickTimerUserUseCase.Result.Success -> Empty.Default
        }
    }

    /**
     * Retrieves a list of members for a timer.
     *
     * @param request The request object containing the timerId and nextPageToken.
     * @return The response object containing the list of users and nextPageToken.
     */
    override suspend fun getMembers(
        request: GetMembersRequest,
    ): GetMembersRequest.Response = authorized {
        val result = getMembersUseCase.execute(
            timerId = TimerId.createOrFail(request.timerId),
            pageToken = request.nextPageToken.nullIfEmpty()?.let { PageToken.accept(it) },
        )

        when (result) {
            GetMembersUseCase.Result.NoAccess -> noAccess()
            is GetMembersUseCase.Result.Success -> GetMembersRequest.Response.create {
                users = result.list.map { it.rs() }
                nextPageToken = result.nextPageToken?.forPublic().orEmpty()
            }
        }
    }

    /**
     * Creates an invite for a member to join a timer.
     *
     * @param request The request containing the timer ID and maximum number of joiners.
     * @return The response containing the invite code.
     */
    override suspend fun createInvite(
        request: InviteMemberRequest,
    ): InviteMemberRequest.Response = authorized {
        val result = createInviteUseCase.execute(
            timerId = TimerId.createOrFail(request.timerId),
            limit = Count.createOrFail(request.maxJoiners),
        )

        when (result) {
            CreateInviteUseCase.Result.NoAccess -> noAccess()
            CreateInviteUseCase.Result.TooManyCreation -> tooManyRequests()
            is CreateInviteUseCase.Result.Success -> InviteMemberRequest.Response.create {
                inviteCode = result.code.string
            }
        }
    }

    /**
     * Retrieves a list of invites for a timer.
     *
     * @param request The request object containing the timer ID and the next page token.
     * @return The response object containing the list of invites and the next page token.
     */
    override suspend fun getInvites(
        request: GetInvitesRequest,
    ): GetInvitesRequest.Response = authorized {
        val result = getInvitesUseCase.execute(
            timerId = TimerId.createOrFail(request.timerId),
            pageToken = request.nextPageToken.nullIfEmpty()?.let { PageToken.accept(it) },
        )

        when (result) {
            GetInvitesUseCase.Result.NoAccess -> noAccess()
            is GetInvitesUseCase.Result.Success -> GetInvitesRequest.Response.create {
                invites = result.page.value.map { it.rs() }
            }
        }
    }

    /**
     * Removes an invite from a timer.
     *
     * @param request The request object containing the timer ID and invite code.
     * @return An instance of the Empty class.
     */
    override suspend fun removeInvite(
        request: RemoveInviteRequest,
    ): Empty = authorized {
        val result = removeInviteUseCase.execute(
            timerId = TimerId.createOrFail(request.timerId),
            code = InviteCode.createOrFail(request.inviteCode),
        )

        when (result) {
            RemoveInviteUseCase.Result.NoAccess -> noAccess()
            RemoveInviteUseCase.Result.NotFound -> notFound()
            RemoveInviteUseCase.Result.Success -> Empty.Default
        }
    }

    /**
     * Removes a timer.
     *
     * @param request The request containing the timer ID.
     * @return An instance of [Empty] indicating the success of the operation.
     */
    override suspend fun removeTimer(
        request: RemoveTimerRequest,
    ): Empty = authorized {
        val result = removeTimerUseCase.execute(TimerId.createOrFail(request.timerId))

        when (result) {
            RemoveTimerUseCase.Result.NotFound -> notFound()
            RemoveTimerUseCase.Result.Success -> Empty.Default
        }
    }
}