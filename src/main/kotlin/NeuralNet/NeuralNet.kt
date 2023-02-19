package NeuralNet

import Snake.Table

data class NeuralNet(val inputNodes: Int, val hiddenNodes: Int, val outputNodes: Int, var useSecond: Boolean) {
    var inputHiddenWeights = Matrix(hiddenNodes, inputNodes + 1)    // whi
    var hiddenSecondWeights: Matrix? = null // whh
    var secondOutputWeights = Matrix(outputNodes, hiddenNodes + 1)  // woh
    
    init {
        inputHiddenWeights.randomize()
        secondOutputWeights.randomize()

        if (useSecond) {
            hiddenSecondWeights = Matrix(hiddenNodes, hiddenNodes + 1)  // whh
            hiddenSecondWeights?.randomize()
        }
    }
    
    fun output(arr: FloatArray): FloatArray {
        val inputs = Matrix.singleColumnMatrixFromArray(arr)
        val inputsBias = inputs.addBias()
        
        val hiddenInputs = inputHiddenWeights.dot(inputsBias)
        val hiddenOutputs = hiddenInputs.activate()
        val hiddenOutputsBias = hiddenOutputs.addBias()

        if (useSecond) {
            val secondInputs = hiddenSecondWeights!!.dot(hiddenOutputsBias)
            val secondOutputs = secondInputs.activate()
            val secondOutputsBias = secondOutputs.addBias()

            val outputInputs = secondOutputWeights.dot(secondOutputsBias)
            val outputs = outputInputs.activate()

            return outputs.toArray()
        } else {
            val outputInputs = secondOutputWeights.dot(hiddenOutputsBias)
            val outputs = outputInputs.activate()

            return outputs.toArray()
        }
    }

    fun crossover(partner: NeuralNet): NeuralNet {
        val child = NeuralNet(inputNodes, hiddenNodes, outputNodes, useSecond)
        child.inputHiddenWeights = inputHiddenWeights.crossover(partner.inputHiddenWeights)
        child.hiddenSecondWeights = hiddenSecondWeights?.crossover(partner.hiddenSecondWeights!!)
        child.secondOutputWeights = secondOutputWeights.crossover(partner.secondOutputWeights)
        return child
    }

    fun mutate(mutationRate: Float) {
        // mutates each weight matrix
        inputHiddenWeights.mutate(mutationRate)
        hiddenSecondWeights?.mutate(mutationRate)
        secondOutputWeights.mutate(mutationRate)
    }

    fun netToTable(): Table {
        val table = Table()
        table.add(*inputHiddenWeights.toArray())
        hiddenSecondWeights.let { if (it != null) table.add(*it.toArray()) }
        table.add(*secondOutputWeights.toArray())
        return table
    }

    fun tableToNet(table: Table) {
        if (table.contents.size == 3) {
            inputHiddenWeights.setFromArray(table.getAt(0).map { it.toFloat() })
            hiddenSecondWeights = Matrix(hiddenNodes, hiddenNodes + 1)
            hiddenSecondWeights!!.setFromArray(table.getAt(1).map { it.toFloat() })
            secondOutputWeights.setFromArray(table.getAt(2).map { it.toFloat() })
            this.useSecond = true
        } else {
            inputHiddenWeights.setFromArray(table.getAt(0).map { it.toFloat() })
            hiddenSecondWeights = null
            secondOutputWeights.setFromArray(table.getAt(1).map { it.toFloat() })
            this.useSecond = false
        }
    }

    fun copyFrom(other: NeuralNet) {
        inputHiddenWeights.copyFrom(other.inputHiddenWeights)
        if (other.hiddenSecondWeights == null) {
            hiddenSecondWeights = null
        } else {
            hiddenSecondWeights = Matrix(hiddenNodes, hiddenNodes + 1)
            hiddenSecondWeights!!.copyFrom(other.hiddenSecondWeights!!)
        }
        secondOutputWeights.copyFrom(other.secondOutputWeights)
    }
}