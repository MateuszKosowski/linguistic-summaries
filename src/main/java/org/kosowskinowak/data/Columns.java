package org.kosowskinowak.data;

import java.util.List;

/** Nazwy kolumn zbioru roboczego (zgodne z {@code selected_columns.md} i sprawozdaniem). */
public final class Columns {

    private Columns() {
    }

    public static final String ID_TRIM = "id_trim";
    public static final String MAKE = "Make";
    public static final String MODEL = "Modle";

    public static final String LENGTH = "length_mm";
    public static final String WHEELBASE = "wheelbase_mm";
    public static final String CURB_WEIGHT = "curb_weight_kg";
    public static final String TRUNK = "minimum_trunk_capacity_l";
    public static final String TORQUE = "maximum_torque_n_m";
    public static final String CAPACITY = "capacity_cm3";
    public static final String ENGINE_HP = "engine_hp";
    public static final String TURNING_CIRCLE = "turning_circle_m";
    public static final String FUEL_CONSUMPTION = "mixed_fuel_consumption_per_100_km_l";
    public static final String FUEL_TANK = "fuel_tank_capacity_l";
    public static final String ACCELERATION = "acceleration_0_100_km/h_s";
    public static final String MAX_SPEED = "max_speed_km_per_h";

    /** 12 atrybutów liczbowych podlegających rozmyciu. */
    public static final List<String> NUMERIC = List.of(
            LENGTH, WHEELBASE, CURB_WEIGHT, TRUNK, TORQUE, CAPACITY, ENGINE_HP,
            TURNING_CIRCLE, FUEL_CONSUMPTION, FUEL_TANK, ACCELERATION, MAX_SPEED);

    /** Wszystkie kolumny w kolejności pliku CSV. */
    public static final List<String> ALL = List.of(
            ID_TRIM, MAKE, MODEL, LENGTH, WHEELBASE, CURB_WEIGHT, TRUNK, TORQUE,
            CAPACITY, ENGINE_HP, TURNING_CIRCLE, FUEL_CONSUMPTION, FUEL_TANK, ACCELERATION, MAX_SPEED);
}
