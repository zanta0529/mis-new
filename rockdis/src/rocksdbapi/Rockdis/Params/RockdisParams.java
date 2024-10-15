package rocksdbapi.Rockdis.Params;

public class RockdisParams {


    public static final String SUCCESSFUL = "OK";


    public static enum Type {
        PRIMARY, SECONDARY;

        public final String raw;
        Type(){
            raw = this.name();
        }
    }
}
