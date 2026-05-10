# -*- coding: utf-8 -*-
"""
Generuje wykresy funkcji przynalezosci dla:
- 12 zmiennych lingwistycznych (atrybuty pojazdow)
- 2 rodzin kwantyfikatorow lingwistycznych (wzgledne i bezwzgledne).

Trapez (a, b, c, d):
    0                       dla x <= a
    (x - a) / (b - a)       dla a <  x <  b
    1                       dla b <= x <= c
    (d - x) / (d - c)       dla c <  x <  d
    0                       dla x >= d

Skrajne etykiety (b. mala / b. duza) sa lewo-/prawo-otwarte: a=b lub c=d.
Sasiednie trapezy nakladaja sie tak, ze suma stopni przynaleznosci ~ 1
dla kazdego x z uniwersum.
"""

from __future__ import annotations
import os
import numpy as np
import matplotlib.pyplot as plt

OUT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "plots")
os.makedirs(OUT_DIR, exist_ok=True)


def trapezoid(x: np.ndarray, a: float, b: float, c: float, d: float) -> np.ndarray:
    x = np.asarray(x, dtype=float)
    y = np.zeros_like(x)
    if b > a:
        m = (x > a) & (x < b)
        y[m] = (x[m] - a) / (b - a)
    if d > c:
        m = (x > c) & (x < d)
        y[m] = (d - x[m]) / (d - c)
    y[(x >= b) & (x <= c)] = 1.0
    if a == b:
        y[x <= a] = 1.0
    if c == d:
        y[x >= c] = 1.0
    return np.clip(y, 0.0, 1.0)


# ---- 12 zmiennych lingwistycznych ----
# (etykieta, a, b, c, d) — kolejnosc rosnaca wartosci atrybutu
VARIABLES = {
    "length_mm": {
        "title": r"Dlugosc pojazdu $L$ [mm]",
        "x_range": (1500, 6500),
        "labels": [
            ("krótki",          1500, 1500, 3500, 3900),
            ("kompaktowy",      3500, 3900, 4200, 4500),
            ("standardowy",     4200, 4500, 4700, 4900),
            ("długi",           4700, 4900, 5100, 5300),
            ("wielkogabarytowy",5100, 5300, 6500, 6500),
        ],
    },
    "wheelbase_mm": {
        "title": r"Rozstaw osi $W$ [mm]",
        "x_range": (1300, 4000),
        "labels": [
            ("zwarty",      1300, 1300, 2300, 2450),
            ("krótki",      2300, 2450, 2550, 2650),
            ("standardowy", 2550, 2650, 2750, 2850),
            ("wydłużony",   2750, 2850, 2950, 3050),
            ("limuzynowy",  2950, 3050, 4000, 4000),
        ],
    },
    "curb_weight_kg": {
        "title": r"Masa wlasna $M$ [kg]",
        "x_range": (500, 3500),
        "labels": [
            ("piórkowy",       500,  500,  900, 1100),
            ("lekki",          900, 1100, 1250, 1400),
            ("średnio ciężki",1250, 1400, 1500, 1650),
            ("ciężki",        1500, 1650, 1850, 2050),
            ("masywny",       1850, 2050, 3500, 3500),
        ],
    },
    "minimum_trunk_capacity_l": {
        "title": r"Pojemnosc bagaznika $B$ [l]",
        "x_range": (0, 1500),
        "labels": [
            ("symboliczny",   0,   0, 200, 300),
            ("ciasny",      200, 300, 350, 400),
            ("standardowy", 350, 400, 450, 500),
            ("pojemny",     450, 500, 600, 700),
            ("ładunkowy",   600, 700, 1500, 1500),
        ],
    },
    "maximum_torque_n_m": {
        "title": r"Moment obrotowy $T$ [Nm]",
        "x_range": (0, 1100),
        "labels": [
            ("wiotki",        0,   0, 100, 150),
            ("miękki",      100, 150, 180, 230),
            ("zrównoważony",180, 230, 280, 350),
            ("mocarny",     280, 350, 450, 550),
            ("potężny",     450, 550, 1100, 1100),
        ],
    },
    "capacity_cm3": {
        "title": r"Pojemnosc skokowa $V$ [cm$^3$]",
        "x_range": (400, 8000),
        "labels": [
            ("miniaturowy",        400,  400, 1200, 1400),
            ("małolitrażowy",     1200, 1400, 1600, 1800),
            ("średniolitrażowy",  1600, 1800, 2000, 2500),
            ("wielkolitrażowy",   2000, 2500, 3500, 4500),
            ("gigantyczny",       3500, 4500, 8000, 8000),
        ],
    },
    "engine_hp": {
        "title": r"Moc silnika $P$ [KM]",
        "x_range": (0, 800),
        "labels": [
            ("anemiczny",   0,   0,  70, 100),
            ("słaby",      70, 100, 120, 150),
            ("przyzwoity",120, 150, 180, 220),
            ("dynamiczny",180, 220, 300, 400),
            ("wyczynowy", 300, 400, 800, 800),
        ],
    },
    "turning_circle_m": {
        "title": r"Srednica zawracania $R$ [m]",
        "x_range": (5, 20),
        "labels": [
            ("zwinny",       5.0,  5.0,  9.5, 10.0),
            ("zwrotny",      9.5, 10.0, 10.5, 11.0),
            ("przeciętny",  10.5, 11.0, 11.3, 11.7),
            ("nieporęczny", 11.3, 11.7, 12.5, 13.5),
            ("ociężały",    12.5, 13.5, 20.0, 20.0),
        ],
    },
    "mixed_fuel_consumption_per_100_km_l": {
        "title": r"Spalanie $F$ [l/100 km]",
        "x_range": (0, 25),
        "labels": [
            ("oszczędny",    0,  0,  4,  5),
            ("ekonomiczny",  4,  5,  6,  7),
            ("umiarkowany",  6,  7,  8, 10),
            ("paliwożerny",  8, 10, 12, 15),
            ("rozrzutny",   12, 15, 25, 25),
        ],
    },
    "fuel_tank_capacity_l": {
        "title": r"Pojemnosc zbiornika $J$ [l]",
        "x_range": (10, 160),
        "labels": [
            ("mały",         10,  10, 40, 45),
            ("niewielki",    40,  45, 50, 55),
            ("standardowy",  50,  55, 65, 70),
            ("pojemny",      65,  70, 80, 90),
            ("ogromny",      80,  90, 160, 160),
        ],
    },
    "acceleration_0_100_s": {
        "title": r"Czas przyspieszenia 0--100 km/h $A$ [s]",
        "x_range": (2, 25),
        "labels": [
            ("rakietowy",   2,  2,  5,  6),
            ("szybki",      5,  6,  8,  9),
            ("przeciętny",  8,  9, 11, 13),
            ("ospały",     11, 13, 15, 17),
            ("ślamazarny", 15, 17, 25, 25),
        ],
    },
    "max_speed_km_per_h": {
        "title": r"Predkosc maksymalna $S$ [km/h]",
        "x_range": (80, 360),
        "labels": [
            ("powolny",      80,  80, 150, 165),
            ("miejski",     150, 165, 180, 195),
            ("autostradowy",180, 195, 210, 230),
            ("wyścigowy",   210, 230, 250, 280),
            ("błyskawiczny",250, 280, 360, 360),
        ],
    },
}

# Tytuly z polskimi znakami (matplotlib obsluguje UTF-8)
PRETTY_TITLE = {
    "length_mm":                          "Długość pojazdu $L$ [mm]",
    "wheelbase_mm":                       "Rozstaw osi $W$ [mm]",
    "curb_weight_kg":                     "Masa własna $M$ [kg]",
    "minimum_trunk_capacity_l":           "Pojemność bagażnika $B$ [l]",
    "maximum_torque_n_m":                 "Moment obrotowy $T$ [Nm]",
    "capacity_cm3":                       "Pojemność skokowa $V$ [cm$^3$]",
    "engine_hp":                          "Moc silnika $P$ [KM]",
    "turning_circle_m":                   "Średnica zawracania $R$ [m]",
    "mixed_fuel_consumption_per_100_km_l": "Spalanie $F$ [l/100 km]",
    "fuel_tank_capacity_l":               "Pojemność zbiornika $J$ [l]",
    "acceleration_0_100_s":               "Czas przyspieszenia 0–100 km/h $A$ [s]",
    "max_speed_km_per_h":                 "Prędkość maksymalna $S$ [km/h]",
}

COLORS = ["#1f77b4", "#2ca02c", "#ff7f0e", "#d62728", "#9467bd"]


def plot_variable(name: str, spec: dict) -> None:
    x_min, x_max = spec["x_range"]
    x = np.linspace(x_min, x_max, 2000)
    fig, ax = plt.subplots(figsize=(7.5, 3.0))
    for (label, a, b, c, d), color in zip(spec["labels"], COLORS):
        y = trapezoid(x, a, b, c, d)
        ax.plot(x, y, color=color, lw=1.6, label=label)
        ax.fill_between(x, 0, y, color=color, alpha=0.10)
    ax.set_xlim(x_min, x_max)
    ax.set_ylim(0, 1.05)
    ax.set_xlabel(PRETTY_TITLE.get(name, spec["title"]))
    ax.set_ylabel(r"$\mu(x)$")
    ax.grid(True, alpha=0.25, linestyle="--")
    ax.legend(loc="upper right", ncol=5, fontsize=8, frameon=False,
              bbox_to_anchor=(1.0, 1.18))
    fig.tight_layout()
    out = os.path.join(OUT_DIR, f"mf_{name}.png")
    fig.savefig(out, dpi=150, bbox_inches="tight")
    plt.close(fig)
    print(f"  zapisano {out}")


# ---- Kwantyfikatory wzgledne (proporcjonalne, X = [0, 1]) ----
RELATIVE_QUANTIFIERS = [
    ("prawie żaden",     0.00, 0.00, 0.05, 0.20),
    ("mniejszość",       0.05, 0.20, 0.30, 0.40),
    ("około połowy",     0.30, 0.40, 0.60, 0.70),
    ("większość",        0.60, 0.70, 0.85, 0.95),
    ("prawie wszystkie", 0.85, 0.95, 1.00, 1.00),
]

# ---- Kwantyfikatory bezwzgledne (X = [0, 25000] - bo zbior ma 23441 rekordow) ----
ABSOLUTE_QUANTIFIERS = [
    ("bardzo mało",        0,     0,  1000,  3000),
    ("kilka tysięcy",   1000,  3000,  6000,  8000),
    ("kilkanaście tysięcy", 6000,  8000, 14000, 16000),
    ("dużo",           14000, 16000, 25000, 25000),
]


def plot_quantifiers(family_name: str, quants, x_range, x_label, file_name) -> None:
    x_min, x_max = x_range
    x = np.linspace(x_min, x_max, 2000)
    fig, ax = plt.subplots(figsize=(7.5, 3.0))
    for (label, a, b, c, d), color in zip(quants, COLORS):
        y = trapezoid(x, a, b, c, d)
        ax.plot(x, y, color=color, lw=1.6, label=label)
        ax.fill_between(x, 0, y, color=color, alpha=0.10)
    ax.set_xlim(x_min, x_max)
    ax.set_ylim(0, 1.05)
    ax.set_xlabel(x_label)
    ax.set_ylabel(r"$\mu_Q(x)$")
    ax.grid(True, alpha=0.25, linestyle="--")
    ax.legend(loc="upper right", ncol=len(quants), fontsize=8, frameon=False,
              bbox_to_anchor=(1.0, 1.18))
    fig.tight_layout()
    out = os.path.join(OUT_DIR, file_name)
    fig.savefig(out, dpi=150, bbox_inches="tight")
    plt.close(fig)
    print(f"  zapisano {out}  ({family_name})")


def main() -> None:
    print("Generowanie wykresow funkcji przynaleznosci dla zmiennych:")
    for name, spec in VARIABLES.items():
        plot_variable(name, spec)

    print("Generowanie wykresow kwantyfikatorow lingwistycznych:")
    plot_quantifiers(
        "wzglednych", RELATIVE_QUANTIFIERS, (0.0, 1.0),
        "udzial obiektow spelniajacych warunek (proporcja r ∈ [0,1])",
        "quantifiers_relative.png",
    )
    plot_quantifiers(
        "bezwzglednych", ABSOLUTE_QUANTIFIERS, (0, 25000),
        "liczba obiektow spelniajacych warunek",
        "quantifiers_absolute.png",
    )

    print("\nGotowe.")


if __name__ == "__main__":
    main()
