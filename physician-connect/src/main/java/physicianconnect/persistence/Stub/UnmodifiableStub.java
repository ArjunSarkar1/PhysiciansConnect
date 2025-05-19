import java.util.*;

abstract class UnmodifiableStub  {
    protected <T> List<T> unmodifiableCopy(List<T> list) {
        return Collections.unmodifiableList(list);
    }

    protected <K, V> Map<K, V> unmodifiableCopy(Map<K, V> map) {
        return Collections.unmodifiableMap(map);
    }
}