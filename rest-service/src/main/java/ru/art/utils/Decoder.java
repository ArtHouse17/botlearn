package ru.art.utils;

import org.hashids.Hashids;
import org.springframework.stereotype.Component;

@Component
public class Decoder {
    private final Hashids hashids;

    public Decoder(Hashids hashids) {
        this.hashids = hashids;
    }

    public Long idOf(String value){
        long[] res = hashids.decode(value);
        if (res != null && res.length > 0) {
            return res[0];
        }
        return null;
    }
}
