package se.mattiashellman.a2x

import android.util.Log

/**
 * Class for containing and calculating incrementally larger prime numbers
 */
class PrimeGenerator(var number: Long = 1) {

    /**
     * Generates the next bigger prime number
     */
    fun generateNext() : Long {
        //Log.d("TestIsPRime", "4: " + isPrime(4).toString())

        do {
            number+=2
        } while (!isPrime(number))

        return number
    }

    /**
     * Determines if a candidate number is prime
     */
    private fun isPrime(candidate: Long): Boolean {
        val sqrt = Math.sqrt(candidate.toDouble()).toLong()
        var i: Long = 3
        while (i <= sqrt) {
            if (candidate % i == 0L) return false
            i += 2
        }
        return true
    }
}