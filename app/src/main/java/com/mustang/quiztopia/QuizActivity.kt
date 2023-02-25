package com.mustang.quiztopia

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.shapes.Shape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mustang.quiztopia.databinding.ActivityQuizBinding
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {

    lateinit var quizBinding: ActivityQuizBinding

    val database = FirebaseDatabase.getInstance()
    val databaseReference = database.reference.child("questions")

    var question = ""
    var answerA = ""
    var answerB = ""
    var answerC = ""
    var answerD = ""
    var correctAnswer = ""
    var questionCount = 0
    var questionNumber = 0

    var userAnswer = ""
    var userCorrect = 0
    var userWrong = 0

    lateinit var timer: CountDownTimer
    private val totalTime = 25000L
    var timerContinue = false
    var leftTime = totalTime

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val scoreRef = database.reference

    val questions = HashSet<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root
        setContentView(view)

        do {
            val number = Random.nextInt(1,21)
            questions.add(number)
            Log.d("number",number.toString())
        } while (questions.size < 5)

        Log.d("numberOfQuestions",questions.toString())

        gameLogic()

        quizBinding.buttonNext.setOnClickListener {

            resetTimer()
            gameLogic()

        }

        quizBinding.buttonFinish.setOnClickListener {

            sendScore()

        }

        quizBinding.textViewA.setOnClickListener {

            pauseTimer()

            userAnswer = "a"
            if (correctAnswer == userAnswer) {

                quizBinding.textViewA.setBackgroundResource(R.drawable.correct_shape)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()

            } else {

                quizBinding.textViewA.setBackgroundResource(R.drawable.wrong_shape)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()

            }
            disableClickableOfOptions()

        }

        quizBinding.textViewB.setOnClickListener {

            pauseTimer()

            userAnswer = "b"
            if (correctAnswer == userAnswer) {

                quizBinding.textViewB.setBackgroundResource(R.drawable.correct_shape)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()

            } else {

                quizBinding.textViewB.setBackgroundResource(R.drawable.wrong_shape)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOptions()

        }

        quizBinding.textViewC.setOnClickListener {

            pauseTimer()

            userAnswer = "c"
            if (correctAnswer == userAnswer) {

                quizBinding.textViewC.setBackgroundResource(R.drawable.correct_shape)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()

            } else {

                quizBinding.textViewC.setBackgroundResource(R.drawable.wrong_shape)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOptions()

        }

        quizBinding.textViewD.setOnClickListener {

            pauseTimer()

            userAnswer = "d"
            if (correctAnswer == userAnswer) {

                quizBinding.textViewD.setBackgroundResource(R.drawable.correct_shape)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()

            } else {

                quizBinding.textViewD.setBackgroundResource(R.drawable.wrong_shape)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOptions()

        }

    }

     private fun gameLogic() {

         restoreOptions()

        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                questionCount = snapshot.childrenCount.toInt()

                if (questionNumber < questions.size) {

                    question = snapshot.child(questions.elementAt(questionNumber).toString()).child("q").value.toString()
                    answerA = snapshot.child(questions.elementAt(questionNumber).toString()).child("a").value.toString()
                    answerB = snapshot.child(questions.elementAt(questionNumber).toString()).child("b").value.toString()
                    answerC = snapshot.child(questions.elementAt(questionNumber).toString()).child("c").value.toString()
                    answerD = snapshot.child(questions.elementAt(questionNumber).toString()).child("d").value.toString()
                    correctAnswer = snapshot.child(questions.elementAt(questionNumber).toString()).child("answer").value.toString()

                    quizBinding.textViewQuestion.text = question
                    quizBinding.textViewA.text = answerA
                    quizBinding.textViewB.text = answerB
                    quizBinding.textViewC.text = answerC
                    quizBinding.textViewD.text = answerD

                    quizBinding.progressBarQuiz.visibility = View.INVISIBLE
                    quizBinding.linearLayoutInfo.visibility = View.VISIBLE
                    quizBinding.linearLayoutQuestion.visibility = View.VISIBLE
                    quizBinding.linearLayoutButtons.visibility = View.VISIBLE

                    startTimer()

                } else {

                    val dialogMessage = AlertDialog.Builder(this@QuizActivity)
                    dialogMessage.setTitle("Quiztopia")
                    dialogMessage.setMessage("Congratulations!!\nYou have answered all the questions. Do you want to see the result?")
                    dialogMessage.setCancelable(false)
                    dialogMessage.setPositiveButton("See Result"){dialogWindow,position ->

                        sendScore()

                    }

                    dialogMessage.setNegativeButton("Play Again"){dialogWindow,position ->

                        val intent = Intent(this@QuizActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    dialogMessage.create().show()

                }

                questionNumber++

            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()

            }

        })

    }

    // Turn correct answer green
    private fun findAnswer() {

        when (correctAnswer) {

            "a" -> quizBinding.textViewA.setBackgroundResource(R.drawable.correct_shape)
            "b" -> quizBinding.textViewB.setBackgroundResource(R.drawable.correct_shape)
            "c" -> quizBinding.textViewC.setBackgroundResource(R.drawable.correct_shape)
            "d" -> quizBinding.textViewD.setBackgroundResource(R.drawable.correct_shape)

        }

    }

    private fun disableClickableOfOptions() {

        quizBinding.textViewA.isClickable = false
        quizBinding.textViewB.isClickable = false
        quizBinding.textViewC.isClickable = false
        quizBinding.textViewD.isClickable = false

    }

    private fun restoreOptions() {

        quizBinding.textViewA.setBackgroundResource(R.drawable.questions_shape)
        quizBinding.textViewB.setBackgroundResource(R.drawable.questions_shape)
        quizBinding.textViewC.setBackgroundResource(R.drawable.questions_shape)
        quizBinding.textViewD.setBackgroundResource(R.drawable.questions_shape)

        quizBinding.textViewA.isClickable = true
        quizBinding.textViewB.isClickable = true
        quizBinding.textViewC.isClickable = true
        quizBinding.textViewD.isClickable = true

    }

    private fun startTimer() {

        timer = object : CountDownTimer(leftTime,1000) {
            override fun onTick(millisUntilFinished: Long) {

                leftTime = millisUntilFinished
                updateCountdownText()

            }

            override fun onFinish() {

                disableClickableOfOptions()
                resetTimer()
                updateCountdownText()
                quizBinding.textViewQuestion.text = "Sorry, Time is up! Continue with next question."
                timerContinue = false

            }


        }.start()

        timerContinue = true

    }

    fun updateCountdownText() {

        val remainingTime : Int = (leftTime/1000).toInt()
        quizBinding.textViewTime.text = remainingTime.toString()

    }

    fun pauseTimer() {

        timer.cancel()
        timerContinue = false

    }

    fun resetTimer() {

        pauseTimer()
        leftTime = totalTime
        updateCountdownText()

    }

    fun sendScore() {

        user?.let {
            val userUID = it.uid
            scoreRef.child("scores").child(userUID).child("correct").setValue(userCorrect)
            scoreRef.child("scores").child(userUID).child("wrong").setValue(userWrong).addOnSuccessListener {

                val intent = Intent(this@QuizActivity,ResultActivity::class.java)
                startActivity(intent)
                finish()

            }
        }


    }

}