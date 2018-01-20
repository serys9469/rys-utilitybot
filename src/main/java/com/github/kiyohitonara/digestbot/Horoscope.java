package com.github.kiyohitonara.digestbot;
import java.util.List;

public class Horoscope {
    private List<Data> data;

    public Horoscope(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Horoscope{" +
                "data=" + data +
                '}';
    }
}
