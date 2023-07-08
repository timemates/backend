package io.timemates.backend.services.settings

import com.google.protobuf.Empty
import io.timemates.api.settings.requests.SetGravatarRequestOuterClass
import io.timemates.api.users.GravatarServiceGrpcKt
import io.timemates.backend.authorization.types.Email
import io.timemates.backend.avatar.usecases.SetGravatarUseCase
import io.timemates.backend.services.common.validation.createOrStatus
import io.timemates.backend.users.types.value.EmailAddress

class GravatarService(
    private val setGravatarUseCase: SetGravatarUseCase
): GravatarServiceGrpcKt.GravatarServiceCoroutineImplBase() {

    override suspend fun setGravatar(request: SetGravatarRequestOuterClass.SetGravatarRequest): Empty {
        setGravatarUseCase.execute(EmailAddress.createOrStatus(request.email.toString()))
        return Empty.getDefaultInstance()
    }
}