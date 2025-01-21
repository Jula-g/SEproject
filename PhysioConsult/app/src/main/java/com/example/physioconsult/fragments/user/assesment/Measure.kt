package com.example.physioconsult.fragments.user.assesment

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import java.io.IOException

class Measure() {
    private val options = AccuratePoseDetectorOptions.Builder()
        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
        .build()

    private val poseDetector = PoseDetection.getClient(options)

    fun detectAndSavePoses(uriList: MutableList<Uri?>, context: Context, onComplete: (List<Pose>) -> Unit) {
        Log.e("resultsFBS", "urilist: ${uriList}")
        val posesList = mutableListOf<Pose>()
        val totalCount = uriList.size
        var completedCount = 0

        for(uri in uriList) {
            if(uri != null) {
                try {
                    val image = InputImage.fromFilePath(context, uri)

                    poseDetector.process(image)
                        .addOnSuccessListener { pose ->
                            posesList.add(pose)
                            completedCount++
                            if(completedCount == totalCount) {
                                onComplete(posesList)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("PoseDetection", "Pose detection failed for URI: $uri", e)

                            completedCount++
                            if (completedCount == totalCount) {
                                onComplete(posesList)
                            }
                        }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("resultsFBS", "failed ${e}")
                    completedCount++
                    if (completedCount == totalCount) {
                        onComplete(posesList)
                    }
                }
            }
        }

        // If no URIs are provided, immediately call the callback with an empty list
        if (uriList.isEmpty()) {
            onComplete(posesList)
        }
    }


    fun conductMeasurements(poses: List<Pose>): Triple<MutableMap<String, Double?>, MutableMap<String, Double?>, MutableMap<String, Double?>> {
        val frontResultsMap = mutableMapOf<String, Double?>()
        val backResultsMap = mutableMapOf<String, Double?>()
        val sideResultsMap = mutableMapOf<String, Double?>()
        var scaledSideResultMap = mutableMapOf<String, Double?>()
        var scaledBackResultsMap = mutableMapOf<String, Double?>()

        val keys = listOf(
            "Hips",
            "Shoulders",
            "Thigh L",
            "Thigh R",
            "Shin L",
            "Shin R",
            "Forearm L",
            "Forearm R",
            "Arm L",
            "Arm R"
        )

        val keys2 = listOf(
            "Thigh L",
            "Shin L",
            "Forearm L",
            "Arm L"
        )

        for((i, pose) in poses.withIndex()){
            val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
            val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
            val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
            val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
            val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
            val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
            val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
            val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
            val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
            val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
            val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
            val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

            // BASE angle
            val ankleL_y = leftAnkle?.position?.y
            val ankleL_x = leftAnkle?.position?.x
            val ankleR_y = rightAnkle?.position?.y
            val ankleR_x = rightAnkle?.position?.x

            val hipL_y = leftHip?.position?.y
            val hipL_x = leftHip?.position?.x
            val hipR_y = rightHip?.position?.y
            val hipR_x = rightHip?.position?.x

            val shoulderL_y = leftShoulder?.position?.y
            val shoulderL_x = leftShoulder?.position?.x
            val shoulderR_y = rightShoulder?.position?.y
            val shoulderR_x = rightShoulder?.position?.x

            val elbowL_y = leftElbow?.position?.y
            val elbowL_x = leftElbow?.position?.x
            val elbowR_y = rightElbow?.position?.y
            val elbowR_x = rightElbow?.position?.x

            val wristL_y = leftWrist?.position?.y
            val wristL_x = leftWrist?.position?.x
            val wristR_y = rightWrist?.position?.y
            val wristR_x = rightWrist?.position?.x

            val kneeL_y = leftKnee?.position?.y
            val kneeL_x = leftKnee?.position?.x
            val kneeR_y = rightKnee?.position?.y
            val kneeR_x = rightKnee?.position?.x


            if(i == 0 || i == 1) {
                val lengthResults = listOf(
                    calculateAngle(ankleL_x,ankleL_y,ankleR_x,ankleR_y,shoulderL_x,shoulderL_y,shoulderR_x,shoulderR_y),
                    calculateAngle(ankleL_x,ankleL_y,ankleR_x,ankleR_y,hipL_x,hipL_y,hipR_x,hipR_y),
                    calculateLength(shoulderL_x, shoulderL_y, elbowL_x, elbowL_y),
                    calculateLength(shoulderR_x, shoulderR_y, elbowR_x, elbowR_y),
                    calculateLength(elbowL_x, elbowL_y, wristL_x, wristL_y),
                    calculateLength(elbowR_x, elbowR_y, wristR_x, wristR_y),
                    calculateLength(hipL_x, hipL_y, kneeL_x, kneeL_y),
                    calculateLength(hipR_x, hipR_y, kneeR_x, kneeR_y),
                    calculateLength(kneeL_x, kneeL_y, ankleL_x, ankleL_y),
                    calculateLength(kneeR_x, kneeR_y, ankleR_x, ankleR_y)
                )

                if(i == 0){
                    keys.zip(lengthResults).forEach{ (key, result) ->
                        frontResultsMap[key] = result
                    }
                } else{
                    keys.zip(lengthResults).forEach{ (key, result) ->
                        backResultsMap[key] = result
                    }

                    Log.d("scaling", "begin back")
                    Log.d("scaling", "back values: ${backResultsMap}")
                    scaledBackResultsMap = scaleMeasurementsUsingReference(frontResultsMap, backResultsMap)
                }
            }else if (i == 2){
                val lengthResults = listOf(
                calculateLength(shoulderL_x, shoulderL_y, elbowL_x, elbowL_y),
                calculateLength(elbowL_x, elbowL_y, wristL_x, wristL_y),
                calculateLength(hipL_x, hipL_y, kneeL_x, kneeL_y),
                calculateLength(kneeL_x, kneeL_y, ankleL_x, ankleL_y),
                )

                keys2.zip(lengthResults).forEach{ (key, result) ->
                    sideResultsMap[key] = result
                }

                Log.d("scaling", "begin side")
                Log.d("scaling", "side values: ${sideResultsMap}")
                scaledSideResultMap = scaleMeasurementsUsingReference(frontResultsMap, sideResultsMap)

            }
        }
        return Triple(frontResultsMap, scaledBackResultsMap, scaledSideResultMap)
    }


    fun calculateAngle(
        leftBase_x: Float?, leftBase_y: Float?, rightBase_x: Float?, rightBase_y: Float?,
        left_x: Float?, left_y: Float?, right_x: Float?, right_y: Float?
    ): Double? {
        if (leftBase_x == null || leftBase_y == null || rightBase_x == null || rightBase_y == null ||
            left_x == null || left_y == null || right_x == null || right_y == null) {
            return null
        }

        // base vector
        val baseVectorX = leftBase_x - rightBase_x
        val baseVectorY = leftBase_y - rightBase_y

        // other vector
        val otherVectorX = left_x - right_x
        val otherVectorY = left_y - right_y


        val dotProduct = baseVectorX * otherVectorX + baseVectorY * otherVectorY

        val baseMagnitude = Math.sqrt((baseVectorX * baseVectorX + baseVectorY * baseVectorY).toDouble())
        val otherMagnitude = Math.sqrt((otherVectorX * otherVectorX + otherVectorY * otherVectorY).toDouble())

        val cosTheta = dotProduct / (baseMagnitude * otherMagnitude)
        val thetaRadians = Math.acos(cosTheta)
        return Math.toDegrees(thetaRadians)
    }

    fun calculateLength(point1_x: Float?, point1_y: Float?, point2_x: Float?, point2_y: Float?): Double?{
        if(point1_x == null || point1_y == null || point2_x == null || point2_y == null){
            return null
        }
        val vx = point1_x - point2_x
        val vy = point1_y - point2_y

        return Math.sqrt((vx * vx + vy * vy).toDouble())
    }

    fun scaleMeasurementsUsingReference(
        frontResults: Map<String, Double?>,
        scaledResults: Map<String, Double?>
    ): MutableMap<String, Double?> {
        // Step 1: Get the reference value for "Arm L" from frontResults and scaledResults
        val armLFront = frontResults["Arm L"]
        val armLScaled = scaledResults["Arm L"]

        if (armLFront == null || armLScaled == null || armLScaled == 0.0) {
            Log.e("ScalingError", "Invalid reference values for scaling (Arm L).")
            return scaledResults.toMutableMap() // Return original if scaling isn't possible
        }

        // Step 2: Calculate the scale factor using "Arm L"
        val scaleFactor = armLFront / armLScaled

        Log.d("scaling", "Front: $frontResults")
        Log.d("scaling", "Scaled: $scaledResults")
        Log.d("scaling", "Scale factor: $scaleFactor")

        // Step 3: Scale all results using the scale factor
        val scaledResultsMap = scaledResults.mapValues { (_, value) ->
            value?.times(scaleFactor)
        }.toMutableMap()

        Log.d("scaling", "Scaled results: $scaledResultsMap")
        return scaledResultsMap
    }


}