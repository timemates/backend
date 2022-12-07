package org.tomadoro.backend.usecases.timers

//class GetLiveUpdatesUseCase(
//    private val timers: TimersRepository,
//    private val timerUpdates: TimerUpdatesRepository,
//    private val startTimer: StartTimerUseCase,
//    private val stopTimer: StopTimerUseCase
//) {
//    suspend operator fun invoke(
//        userId: UsersRepository.UserId,
//        timerId: TimersRepository.TimerId,
//        commands: Flow<Command>,
//        coroutineScope: CoroutineScope,
//    ): Result {
//        if (!timers.isMemberOf(userId, timerId))
//            return Result.NoAccess
//
//        val updates = MutableSharedFlow<TimerUpdatesRepository.Update>()
//
//        coroutineScope.launch {
//            commands.collectLatest {
//                when (it) {
//                    is Command.Start -> startTimer(userId, timerId)
//                    is Command.Stop -> stopTimer(userId, timerId)
//                }
//            }
//        }
//
//        coroutineScope.launch {
//            timerUpdates.receiveUpdates(timerId).collectLatest {
//                updates.emit(it)
//            }
//        }
//
//        return Result.Success(
//            updates
//        )
//    }
//
//    sealed interface Command {
//        object Start : Command
//        object Stop : Command
//    }
//
//    sealed interface Result {
//        object NoAccess : Result
//
//        @JvmInline
//        value class Success(val flow: Flow<TimerUpdatesRepository.Update>) : Result
//    }
//}