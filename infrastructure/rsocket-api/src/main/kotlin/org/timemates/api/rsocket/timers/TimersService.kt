package org.timemates.api.rsocket.timers

import com.google.protobuf.Empty
import org.timemates.api.rsocket.internal.*
import org.timemates.api.rsocket.users.rs
import org.timemates.api.timers.members.invites.requests.GetInvitesRequest
import org.timemates.api.timers.members.invites.requests.InviteMemberRequest
import org.timemates.api.timers.members.invites.requests.RemoveInviteRequest
import org.timemates.api.timers.members.requests.GetMembersRequest
import org.timemates.api.timers.members.requests.KickMemberRequest
import org.timemates.api.timers.requests.*
import org.timemates.api.timers.types.Timer
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.timers.domain.usecases.*
import org.timemates.backend.timers.domain.usecases.members.GetMembersUseCase
import org.timemates.backend.timers.domain.usecases.members.KickTimerUserUseCase
import org.timemates.backend.timers.domain.usecases.members.invites.CreateInviteUseCase
import org.timemates.backend.timers.domain.usecases.members.invites.GetInvitesUseCase
import org.timemates.backend.timers.domain.usecases.members.invites.JoinByInviteUseCase
import org.timemates.backend.timers.domain.usecases.members.invites.RemoveInviteUseCase
import org.timemates.backend.types.common.value.Count
import org.timemates.backend.types.common.value.PageSize
import org.timemates.backend.types.timers.TimerSettings
import org.timemates.backend.types.timers.value.InviteCode
import org.timemates.backend.types.timers.value.TimerDescription
import org.timemates.backend.types.timers.value.TimerId
import org.timemates.backend.types.timers.value.TimerName
import org.timemates.backend.types.users.value.UserId
import kotlin.time.Duration.Companion.milliseconds
import org.timemates.api.timers.TimersService as RSTimersService

/**
 * A service class that provides various timer-related functionality.
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
    private val joinTimerByInvite: JoinByInviteUseCase,
) : RSTimersService() {
    /**
     * Creates a timer with the provided request parameters.
     *
     * @param request The request object that contains the name, description, and settings of the timer.
     * @return The response object containing the ID of the created timer.
     */
    override suspend fun createTimer(
        request: CreateTimerRequest,
    ): CreateTimerRequest.Response {
        val result = createTimerUseCase.execute(
            auth = getAuthorization(),
            name = TimerName.createOrFail(request.name),
            description = TimerDescription.createOrFail(request.description),
            settings = request.settings?.core() ?: TimerSettings.Default,
        )

        return when (result) {
            is CreateTimerUseCase.Result.Success -> CreateTimerRequest.Response {
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
    ): Timer {
        val result = getTimerUseCase.execute(getAuthorization(), TimerId.createOrFail(request.timerId))

        return when (result) {
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
    ): GetTimersRequest.Response {
        val result = getTimersUseCase.execute(
            auth = getAuthorization(),
            pageToken = request.pageToken.nullIfEmpty()?.let { PageToken.accept(it) },
            pageSize = request.pageSize.takeIf { it > 0 }?.let { PageSize.createOrFail(it) } ?: PageSize.DEFAULT,
        )

        return when (result) {
            is GetTimersUseCase.Result.Success -> GetTimersRequest.Response {
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
    ): Empty {
        val result = setTimerInfoUseCase.execute(
            auth = getAuthorization(),
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

        return when (result) {
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
    override suspend fun kickMember(request: KickMemberRequest): Empty {
        val result = kickTimerUserUseCase.execute(
            getAuthorization(),
            TimerId.createOrFail(request.timerId),
            UserId.createOrFail(request.userId),
        )

        return when (result) {
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
    ): GetMembersRequest.Response {
        val result = getMembersUseCase.execute(
            auth = getAuthorization(),
            timerId = TimerId.createOrFail(request.timerId),
            pageToken = request.pageToken.nullIfEmpty()?.let { PageToken.accept(it) },
            pageSize = request.pageSize.takeIf { it > 0 }?.let { PageSize.createOrFail(it) } ?: PageSize.DEFAULT,
        )

        return when (result) {
            GetMembersUseCase.Result.NoAccess -> noAccess()
            is GetMembersUseCase.Result.Success -> GetMembersRequest.Response {
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
    ): InviteMemberRequest.Response {
        val result = createInviteUseCase.execute(
            auth = getAuthorization(),
            timerId = TimerId.createOrFail(request.timerId),
            limit = Count.createOrFail(request.maxJoiners),
        )

        return when (result) {
            CreateInviteUseCase.Result.NoAccess -> noAccess()
            CreateInviteUseCase.Result.TooManyCreation -> tooManyRequests()
            is CreateInviteUseCase.Result.Success -> InviteMemberRequest.Response {
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
    ): GetInvitesRequest.Response {
        val result = getInvitesUseCase.execute(
            auth = getAuthorization(),
            timerId = TimerId.createOrFail(request.timerId),
            pageToken = request.pageToken.nullIfEmpty()?.let { PageToken.accept(it) },
            pageSize = request.pageSize.takeIf { it > 0 }?.let { PageSize.createOrFail(it) } ?: PageSize.DEFAULT,
        )

        return when (result) {
            GetInvitesUseCase.Result.NoAccess -> noAccess()
            is GetInvitesUseCase.Result.Success -> GetInvitesRequest.Response {
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
    ): Empty {
        val result = removeInviteUseCase.execute(
            auth = getAuthorization(),
            timerId = TimerId.createOrFail(request.timerId),
            code = InviteCode.createOrFail(request.inviteCode),
        )

        return when (result) {
            RemoveInviteUseCase.Result.NoAccess -> noAccess()
            RemoveInviteUseCase.Result.NotFound -> notFound()
            RemoveInviteUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun joinByInvite(
        request: JoinTimerByInviteCodeRequest,
    ): JoinTimerByInviteCodeRequest.Response {
        val result = joinTimerByInvite.execute(
            getAuthorization(),
            InviteCode.createOrFail(request.inviteCode),
        )

        return when (result) {
            JoinByInviteUseCase.Result.NotFound -> notFound()
            is JoinByInviteUseCase.Result.Success -> JoinTimerByInviteCodeRequest.Response {
                timer = result.timer.rs()
            }
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
    ): Empty {
        val result = removeTimerUseCase.execute(
            getAuthorization(),
            TimerId.createOrFail(request.timerId),
        )

        return when (result) {
            RemoveTimerUseCase.Result.NotFound -> notFound()
            RemoveTimerUseCase.Result.Success -> Empty.Default
        }
    }
}