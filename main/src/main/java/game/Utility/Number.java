package game.Utility;

public class Number {

    /**
     * count number of digits of input number
     * @param number input number must >= 0
     * @return number of digits of input number
     */
    public static int count_digit(long number){
        if(number == 0) return 0;
        return (int)Math.floor(Math.log10(number)) + 1;
    }

    /**
     * count the number of even numbers in [left, right] by calling count_odd_between
     * can subtract in from number of numbers in interval
     * @param left lower bound inclusive
     * @param right upper bound inclusive
     * @return amount of even numbers in interval [left, right]
     */
    public static long count_even_between(long left, long right){
        return 1 + right - left - count_odd_between(left, right);
    }

    /**
     * count the number of odd numbers in [left, right]
     * @param left lower bound inclusive
     * @param right upper bound inclusive
     * @return amount of odd numbers in interval [left, right]
     */
    public static long count_odd_between(long left, long right){
        return (right - left + 1) / 2 + left % 2;
    }
}