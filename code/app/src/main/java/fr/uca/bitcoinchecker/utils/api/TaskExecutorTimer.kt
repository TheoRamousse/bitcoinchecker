package fr.uca.bitcoinchecker.utils.api

import java.util.*

class TaskExecutorTimer {
    companion object{
        fun executeEachNSeconds(function : () -> Unit, timeInSeconds : Int){
            val timer = Timer()
            val timedTask: TimerTask = object : TimerTask() {
                override fun run() {
                    function.invoke()
                }
            }

            timer.schedule(timedTask, 0L, 1000 * timeInSeconds.toLong())
        }
    }
}