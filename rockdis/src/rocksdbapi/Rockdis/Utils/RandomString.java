package rocksdbapi.Rockdis.Utils;

/**
 * Created by kkw on 2020/1/17.
 */
public class RandomString {

    // function to generate a random string of length n
    public static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public static void main(String[] args)
    {

        // Get the size n
        int n = 200;

        // Get and display the alphanumeric string
        System.out.println(RandomString
                .getAlphaNumericString(n));
    }
}
