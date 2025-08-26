# TFLite Text Classifier Android App

------------------------------------------------------------------------

This Android app leverages a TensorFlow Lite model for on-device
classification of social media posts into 11 categories, including
technology, sports, finance, and more. Built with Kotlin and Jetpack
Compose, it ensures efficient, privacy-focused inference without server
dependencies.

[![License:
MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![TensorFlow
Lite](https://img.shields.io/badge/TensorFlow-Lite-blue.svg)](https://www.tensorflow.org/lite)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blueviolet.svg)](https://kotlinlang.org/)

[![Jetpack
Compose](https://img.shields.io/badge/Jetpack-Compose-green.svg)](https://developer.android.com/jetpack/compose)

[![Android](https://img.shields.io/badge/Android-14+-brightgreen.svg)](https://developer.android.com/)

------------------------------------------------------------------------

## Features

-    **On-device classification** using TensorFlow Lite\
-    **Jetpack Compose UI** for modern Android apps\
-    **Multi-line input** for flexible text entry\
-    **Probability distribution** for each category\
-    **Fast & lightweight inference** with no server calls\
-    **Manual tokenizer** with `vocab.txt` for reproducibility

------------------------------------------------------------------------

## Training Notebook -- `model.ipynb`

The repository includes a Jupyter notebook **`Android.ipynb`**,
containing the full **training pipeline** for the classifier:

-    Load & preprocess training data\
-    Tokenization with `TextVectorization`\
-    Build & train a Keras model\
-    Convert the model to `.tflite` format\
-    Export `vocab.txt` and `labels.txt` for Android usage

Use this notebook to retrain or fine-tune your model while ensuring
**consistency** between training and deployment.

------------------------------------------------------------------------

## Model Overview

-   **Model file**: `model_with_softmax.tflite`\
-   **Input**: `int32[1, 50]` → tokenized & padded sequence\
-   **Output**: `float32[1, 10]` → probability vector (softmax)\
-   **Tokenizer**: `vocab.txt` from training (`TextVectorization`)\
-   **Labels**: `labels.txt` with 11 categories

------------------------------------------------------------------------

## Folder Structure

    app/
     ├── assets/
     │     ├── model_with_softmax.tflite
     │     ├── vocab.txt
     │     └── labels.txt
     └── res/
           └── layout/ (if legacy XML UI is present)

------------------------------------------------------------------------

## Setup Instructions

1.  Add dependencies in `build.gradle`:

``` gradle
implementation 'org.tensorflow:tensorflow-lite:2.12.0'
implementation 'org.tensorflow:tensorflow-lite-support:0.3.1'
```

2.  Copy required files to `app/src/main/assets/`:
   -   `model_with_softmax.tflite`
   -   `vocab.txt`
   -   `labels.txt`
3.  Initialize the classifier in Kotlin:

``` kotlin
TextClassifier.initialize(context)
```

------------------------------------------------------------------------

## Example Usage (Jetpack Compose)

``` kotlin
val text by remember { mutableStateOf("") }

Column {
    TextField(
        value = text,
        onValueChange = { text = it },
        placeholder = { Text("Enter your post...") },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        maxLines = 5
    )

    Button(onClick = {
        val result = TextClassifier.predictAll(text)
        val formatted = result.joinToString("\n") { (label, prob) ->
            "$label: ${"%.2f".format(prob * 100)}%"
        }
        Log.d("Prediction", formatted)
    }) {
        Text("Classify")
    }
}
```

------------------------------------------------------------------------

## Notes

-   The `.tflite` model **must include softmax**\
-   `vocab.txt` and `labels.txt` must match training data\
-   Assets must be placed in `app/src/main/assets/` (not `res/`)\
-   Use `coerceIn()` to ensure predictions remain between `0–1`

------------------------------------------------------------------------

## License

MIT --- Free to use, modify, and distribute.
