package com.example.tfliteapp

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

object TextClassifier {

    private lateinit var interpreter: Interpreter
    private lateinit var vocab: Map<String, Int>
    private lateinit var labels: List<String>

    fun initialize(context: Context) {
        val model = FileUtil.loadMappedFile(context, "model_with_softmax.tflite")
        interpreter = Interpreter(model)
        vocab = loadVocab(context)
        labels = loadLabels(context)
    }

    private fun loadVocab(context: Context): Map<String, Int> {
        return context.assets.open("vocab.txt").bufferedReader().useLines { lines ->
            lines.withIndex().associate { (index, word) -> word to index }
        }
    }

    private fun loadLabels(context: Context): List<String> {
        return context.assets.open("labels.txt").bufferedReader().readLines()
    }

    fun predict(text: String): String {
        val input = preprocess(text, vocab)
        val output = Array(1) { FloatArray(labels.size) }
        interpreter.run(arrayOf(input), output)
        val probs = output[0]

        // Zip labels with probabilities and sort
        val sorted = labels.zip(probs.toList())
            .sortedByDescending { it.second }

        // Format as string: each line "label: 92.34%"
        return sorted.joinToString(separator = "\n") { (label, prob) ->
            "$label: ${"%.2f".format(prob.coerceIn(0f, 1f) * 100)}%"
        }
    }



    private fun preprocess(text: String, vocab: Map<String, Int>, maxLen: Int = 50): IntArray {
        val tokens = text.lowercase()
            .replace(Regex("[^a-z0-9 ]"), "")
            .split(" ")
        val unk = vocab["[UNK]"] ?: 0
        val indices = tokens.map { vocab[it] ?: unk }
        val padded = IntArray(maxLen)
        for (i in indices.indices.take(maxLen)) {
            padded[i] = indices[i]
        }
        return padded
    }
}
