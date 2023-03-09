package SushiGo

enum class CardGroups(val bgColor: String) {
    Nigiri("\u001b[48;5;220m"),     // 3 or 220
    Maki("\u001b[48;5;196m"),         // 1
    Tempura("\u001b[48;5;13m"),     // 5 or 13
    Sashimi("\u001b[48;5;118m"),     // 10
    Dumplings("\u001b[48;5;39m"),  // 14 or 123
    Pudding("\u001b[48;5;225m"),     // 218
    Chopsticks("\u001b[48;5;51m")  // 75
} 

enum class Cards(val symbol: String, val group: CardGroups) {
    Nigiri1("N1", CardGroups.Nigiri),
    Nigiri2("N2", CardGroups.Nigiri),
    Nigiri3("N3", CardGroups.Nigiri),
    Wasabi("Wa", CardGroups.Nigiri),
    Maki1("M1", CardGroups.Maki),
    Maki2("M2", CardGroups.Maki),
    Maki3("M3", CardGroups.Maki),
    Tempura("Te", CardGroups.Tempura),
    Sashimi("Sa", CardGroups.Sashimi),
    Dumplings("Du", CardGroups.Dumplings),
    Pudding("Pu", CardGroups.Pudding),
    Chopsticks("Ch", CardGroups.Chopsticks)
}