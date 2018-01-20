package com.github.kiyohitonara.digestbot;

public class Fortune {
    private Horoscope horoscope;

    public Fortune(Horoscope horoscope) {
        this.horoscope = horoscope;
    }

    public Horoscope getHoroscope() {
        return horoscope;
    }

    public void setHoroscope(Horoscope horoscope) {
        this.horoscope = horoscope;
    }

    @Override
    public String toString() {
        return "Fortune{" +
                "horoscope=" + horoscope +
                '}';
    }
}
