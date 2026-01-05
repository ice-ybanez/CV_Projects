package store.utilities;

public class StoreSerializer {

    private static final String FILE_NAME = "storedata.dat";

    public static void saveAll(StoreData data) {
        FileUtil.writeObjectToFile(data, FILE_NAME);
    }

    public static StoreData loadAll() {
        Object obj = FileUtil.readObjectFromFile(FILE_NAME);
        if (obj instanceof StoreData) {
            return (StoreData) obj;
        }
        return null;
    }
}
