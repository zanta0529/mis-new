package rocksdbapi.Rockdis.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MathOverflowPreventor {

    private static Logger logger = LoggerFactory.getLogger(MathOverflowPreventor.class);
    
    public static final int safeAdd(int left, int right) {
        logger.debug("[Check]Arithmetic overflow Check.");
        if (right > 0 ? left > Integer.MAX_VALUE - right
                : left < Integer.MIN_VALUE - right) {
            throw new ArithmeticException("Integer overflow");
        }

        return left + right;
    }
}
