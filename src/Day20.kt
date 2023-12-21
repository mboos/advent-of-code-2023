import java.util.LinkedList
import java.lang.Thread

enum class Pulse {
    HIGH, LOW
}

data class PulseSignal(val source: PulseModule, val destination: PulseModule, val pulse: Pulse) {}

abstract class PulseModule(val name: String) {
    val outputs: MutableList<PulseModule> = mutableListOf()
    val inputs: MutableList<PulseModule> = mutableListOf()
    abstract fun processPulse(input: PulseSignal): List<PulseSignal>
    abstract fun isOn(): Boolean
}

class FlipFlop(name: String): PulseModule(name) {
    var on = false

    override fun processPulse(input: PulseSignal): List<PulseSignal> {
        if (input.pulse == Pulse.HIGH) {
            return listOf()
        }
        on = !on
        return outputs.map { PulseSignal(this, it, if (on) Pulse.HIGH else Pulse.LOW) }
    }

    override fun isOn(): Boolean = on
}

class Conjunction(name: String): PulseModule(name) {
    val inputMemory: MutableMap<String, Pulse> = mutableMapOf()
    var triggeredLow = false

    override fun processPulse(input: PulseSignal): List<PulseSignal> {
        inputMemory[input.source.name] = input.pulse
        val output = if (inputs.all { inputMemory.getOrPut(it.name) {Pulse.LOW} == Pulse.HIGH }) Pulse.LOW else Pulse.HIGH
        if (output == Pulse.LOW) {
            triggeredLow = true
        }
        return outputs.map { PulseSignal(this, it, output) }
    }

    override fun isOn(): Boolean = false
}

class Broadcaster(name: String): PulseModule(name) {
    override fun processPulse(input: PulseSignal): List<PulseSignal> {
        return outputs.map { PulseSignal(this, it, input.pulse) }
    }

    override fun isOn(): Boolean = false
}

class Sink(name: String): PulseModule(name) {
    var triggeredLow = false

    override fun processPulse(input: PulseSignal): List<PulseSignal> {
        if (input.pulse == Pulse.LOW) {
            triggeredLow = true
        }
        return listOf()
    }

    override fun isOn(): Boolean = false
}

class Button(): PulseModule("button") {
    override fun processPulse(input: PulseSignal): List<PulseSignal> {
        return listOf()
    }

    override fun isOn(): Boolean = false
}

fun buttonMash(first: PulseModule, exitCondition: (Int) -> Boolean): Triple<Int, Int, Int> {
    val button = Button()
    var count = 0
    var highs = 0
    var lows = 0
    val queue = LinkedList<PulseSignal>()
    while (true) {
        count += 1
        queue.add(PulseSignal(button, first, Pulse.LOW))
        while (!queue.isEmpty()) {
            val signal = queue.poll()
            if (signal.pulse == Pulse.LOW) {
                lows += 1
            } else {
                highs += 1
            }
            val response = signal.destination.processPulse(signal)
            queue.addAll(response)
        }
        if (exitCondition(count)) {
            break
        }
    }
    return Triple(count, highs, lows)
}

fun readModules(input: List<String>): Map<String, PulseModule> {
    val moduleMap: MutableMap<String, PulseModule> = input.map {
        val name = it.split(" -> ").first().trim('%', '&')
        lateinit var pulseModule: PulseModule
        if (it[0] == '%') {
            pulseModule = FlipFlop(name)
        } else if (it[0] == '&') {
            pulseModule = Conjunction(name)
        } else {
            pulseModule = Broadcaster(name)
        }
        name to pulseModule
    }.toMap().toMutableMap()
    
    input.forEach {
        val name = it.split(" -> ").first().trim('%', '&')
        val source = moduleMap[name]!!
        it.split(" -> ").last().split(", ").forEach {
            destName ->
            val dest = moduleMap.getOrPut(destName) { Sink(destName) }
            source.outputs.add(dest)
            dest.inputs.add(source)
        }
    }
    return moduleMap
}

fun getConjunctionReset(input: List<String>, name: String): Int {
    val moduleMap = readModules(input)       

    val modules = moduleMap.values.toList()
    val broadcaster = moduleMap["broadcaster"]!! as Broadcaster
    val conj = moduleMap[name]!! as Conjunction

    val (count, _, _) = buttonMash(broadcaster) { c ->
        conj.triggeredLow }

    return count
}

fun main() {
    fun part1(input: List<String>): Int {
        val moduleMap = readModules(input)       

        val modules = moduleMap.values.toList()
        val broadcaster = moduleMap["broadcaster"]!! as Broadcaster

        val (count, cycleHighs, cycleLows) = buttonMash(broadcaster) { c -> c >= 1000 || modules.all { !it.isOn() } }
        val numCycles: Int = 1000 / count
        val remainder = 1000 % count

        if (remainder == 0) {
            return (numCycles * cycleHighs) * (numCycles * cycleLows)
        }

        val (_, remainderHighs, remainderLows) = buttonMash(broadcaster) { c -> c < remainder }

        return (numCycles * cycleHighs + remainderHighs) * (numCycles * cycleLows + remainderLows)
    }

    fun part2(input: List<String>): Long {
        val jj = getConjunctionReset(input, "jj")
        val gf = getConjunctionReset(input, "gf")
        val xz = getConjunctionReset(input, "xz")
        val bz = getConjunctionReset(input, "bz")
        val numbers = listOf(jj, gf, xz, bz).map {it.toLong()}
        return lcm(numbers)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 11687500)

    val input = readInput("Day20")
    part1(input).println()
    Thread.sleep(1000)
    part2(input).println()
}
