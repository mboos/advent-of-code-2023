enum class ValidationResult {
    ACCEPT, REJECT, INDETERMINATE
}

enum class Operator {
    LESS_THAN, GREATER_THAN, ACCEPT, REJECT, COMMAND
}

data class XmasRange (
        val xmin: Int = 1, val xmax: Int = 4000, 
        val mmin: Int = 1, val mmax: Int = 4000,
        val amin: Int = 1, val amax: Int = 4000,
        val smin: Int = 1, val smax: Int = 4000) {

    val size: Long
        get() {
            if (empty) {
                return 0
            }
            return xrange * mrange * arange * srange
        }
    
    val xrange: Long
        get() = xmax - xmin + 1L
    
    val mrange: Long
        get() = mmax - mmin + 1L

    val arange: Long
        get() = amax - amin + 1L
    
    val srange: Long
        get() = smax - smin + 1L

    val empty: Boolean
        get() = xrange <= 0 || mrange <= 0 || arange <= 0 || srange <= 0

    override fun toString(): String {
        return "[X(${xmin}, ${xmax}) M(${mmin}, ${mmax}) A(${amin}, ${amax}) S(${smin}, ${smax})]"
    }

    override fun equals(other: Any?): Boolean {
        if(other is XmasRange) {
            return (xmin == other.xmin && xmax == other.xmax && 
                    mmin == other.mmin && mmax == other.mmax && 
                    amin == other.amin && amax == other.amax && 
                    smin == other.smin && smax == other.smax)
        }
        return false
    }

    fun modMax(variable: Char, value: Int): XmasRange {
        val newX = if (variable == 'x' && value < xmax) value else xmax
        val newM = if (variable == 'm' && value < mmax) value else mmax
        val newA = if (variable == 'a' && value < amax) value else amax
        val newS = if (variable == 's' && value < smax) value else smax
        return XmasRange(xmin, newX, mmin, newM, amin, newA, smin, newS)
    }

    fun modMin(variable: Char, value: Int): XmasRange {
        val newX = if (variable == 'x' && value > xmin) value else xmin
        val newM = if (variable == 'm' && value > mmin) value else mmin
        val newA = if (variable == 'a' && value > amin) value else amin
        val newS = if (variable == 's' && value > smin) value else smin
        return XmasRange(newX, xmax, newM, mmax, newA, amax, newS, smax)
    }

    fun apply(fn: Function, positive: Boolean): XmasRange {
        if (fn.op == Operator.LESS_THAN) {
            var variable = fn.variable!!
            var value = fn.value!!
            if (positive) {
                return modMax(variable, value - 1)
            } else {
                return modMin(variable, value)
            }
        } else if (fn.op == Operator.GREATER_THAN) {
            var variable = fn.variable!!
            var value = fn.value!!
            if (positive) {
                return modMin(variable, value + 1)
            } else {
                return modMax(variable, value)
            }
        }
        return this
    }

    fun intersect(other: XmasRange): XmasRange {
        return XmasRange(
            if (xmin > other.xmin) xmin else other.xmin,
            if (xmax < other.xmax) xmax else other.xmax,
            if (mmin > other.mmin) mmin else other.mmin,
            if (mmax < other.mmax) mmax else other.mmax,
            if (amin > other.amin) amin else other.amin,
            if (amax < other.amax) amax else other.amax,
            if (smin > other.smin) smin else other.smin,
            if (smax < other.smax) smax else other.smax
        )
    }
}

class Command(input: String, val commands: Map<String, Command>) {
    val functions: List<Function>
    init {
        functions = input.split(",").map { Function(it, commands) }
    }

    fun evaluate(values: Map<Char, Int>): Boolean {
        functions.forEach {
            val result = it.evaluate(values)
            if (result == ValidationResult.ACCEPT) {
                return true
            } else if (result == ValidationResult.REJECT) {
                return false
            }
        }
        return false // Needs a default behaviour
    }

    var _catalogue: List<List<Pair<Function, Boolean>>>? = null
    fun catalogue(): List<List<Pair<Function, Boolean>>>  {
        if (_catalogue == null) {
            _catalogue = functions.flatMapIndexed { i, fn ->
                fn.catalogue().map { candidate -> 
                    functions.subList(0, i).map { Pair(it, false) }  + (if (fn.isConditional()) listOf(Pair(fn, true)) else listOf()) + candidate 
                }
            }
        }
        return _catalogue!!
    }
}

class Function(val input: String, val commands: Map<String, Command>) {
    val op: Operator
    val variable: Char?
    val value: Int?
    val subFunction: Function?
    val command: String?
    init {
        val lessIndex = input.indexOf('<')
        val greaterIndex = input.indexOf('>')
        if (lessIndex != -1) {
            var colonIndex = input.indexOf(':')
            op = Operator.LESS_THAN
            variable = input[0]
            value = input.substring(lessIndex+1, colonIndex).toInt()
            subFunction = Function(input.substring(colonIndex+1), commands)
            command = null
        } else if (greaterIndex != -1) {
            var colonIndex = input.indexOf(':')
            op = Operator.GREATER_THAN
            variable = input[0]
            value = input.substring(greaterIndex+1, colonIndex).toInt()
            subFunction = Function(input.substring(colonIndex+1), commands)
            command = null
        } else if (input == "A") {
            op = Operator.ACCEPT
            variable = null
            value = null
            subFunction = null
            command = null
        } else if (input == "R") {
            op = Operator.REJECT
            variable = null
            value = null
            subFunction = null
            command = null
        } else {
            op = Operator.COMMAND
            variable = null
            value = null
            subFunction = null
            command = input
        }
    }

    fun isConditional(): Boolean {
        return op == Operator.LESS_THAN || op == Operator.GREATER_THAN
    }

    fun evaluate(values: Map<Char, Int>): ValidationResult {
        if (op == Operator.ACCEPT) {
            return ValidationResult.ACCEPT
        } else if (op == Operator.REJECT) {
            return ValidationResult.REJECT
        } else if (op == Operator.LESS_THAN) {
            if (values[variable]!! < value!!) {
                return subFunction!!.evaluate(values)
            }
            return ValidationResult.INDETERMINATE
        } else if (op == Operator.GREATER_THAN) {
            if (values[variable]!! > value!!) {
                return subFunction!!.evaluate(values)
            }
            return ValidationResult.INDETERMINATE
        } else /* if (op == Operator.COMMAND) */ {
            return if (commands[command]!!.evaluate(values)) ValidationResult.ACCEPT else ValidationResult.REJECT
        }
    }

    var _catalogue: List<List<Pair<Function, Boolean>>>? = null
    fun catalogue(): List<List<Pair<Function, Boolean>>> {
        if (_catalogue == null) {
            if (op == Operator.REJECT) {
                _catalogue = listOf()
            } else if (op == Operator.ACCEPT) {
                _catalogue = listOf(listOf())
            } else if (op == Operator.LESS_THAN || op == Operator.GREATER_THAN) {
                _catalogue = subFunction!!.catalogue()
            } else {
                _catalogue = commands[command]!!.catalogue()
            }
        }
        return _catalogue!!
    }
    override fun toString(): String = input
}

fun main() {
    fun part1(input: List<String>): Int {
        val commands: MutableMap<String, Command> = mutableMapOf()
        val values: MutableList<Map<Char, Int>> = mutableListOf()
        val commandPattern = "(?<label>[a-z]+)\\{(?<expression>.+)\\}".toRegex()
        val valuesPattern = "(?<variable>[xmas])=(?<value>\\d+)".toRegex()

        input.forEach {
            val commandMatch = commandPattern.find(it)
            if (commandMatch != null) {
                commands[commandMatch.groups["label"]!!.value] = Command(
                    commandMatch.groups["expression"]!!.value,
                    commands)
            } else {
                val valueMatch = valuesPattern.findAll(it).toList()
                if (valueMatch.size == 4) {
                    values.add(valueMatch.map { it.groups["variable"]!!.value[0] to it.groups["value"]!!.value.toInt() }.toMap())
                }
            }
        }

        return values.filter { commands["in"]!!.evaluate(it) }.flatMap{ it.values }.sum()
    }

    fun part2(input: List<String>): Long {
        val commands: MutableMap<String, Command> = mutableMapOf()
        val commandPattern = "(?<label>[a-z]+)\\{(?<expression>.+)\\}".toRegex()

        input.forEach {
            val commandMatch = commandPattern.find(it)
            if (commandMatch != null) {
                commands[commandMatch.groups["label"]!!.value] = Command(
                    commandMatch.groups["expression"]!!.value,
                    commands)
            }
        }

        val catalogue = commands["in"]!!.catalogue()
        val ranges = catalogue.map {
            path ->
            path.fold(XmasRange()) {
                acc, step ->
                val (fn, positive) = step
                acc.apply(fn, positive)
            }
        }.filter { !it.empty }
        
        var i = 0
        val (include, exclude) = ranges.fold(
            Pair(listOf(), listOf())) {
            prev: Pair<List<XmasRange>, List<XmasRange>>, range: XmasRange ->
            val (include, exclude) = prev
            i += 1
            Pair(
                listOf(range) + include + exclude.map { it.intersect(range) }.filter { !it.empty },
                exclude + include.map { it.intersect(range) }.filter { !it.empty }
            )
        }
        return include.map { it.size }.sum() - exclude.map { it.size }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000)

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}
