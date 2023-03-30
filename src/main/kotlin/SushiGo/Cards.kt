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
    Chopsticks("Ch", CardGroups.Chopsticks);

    companion object {

        fun getFromSymbol(symbol: String) = Cards.values().firstOrNull { it.symbol == symbol }
        
        fun setNewDeck(deck: MutableList<Cards>) {
            deck.clear()

            repeat(5) { deck.add(Nigiri1) }
            repeat(5) { deck.add(Nigiri2) }
            repeat(10) { deck.add(Nigiri3) }
            repeat(6) { deck.add(Wasabi) }
            repeat(6) { deck.add(Maki1) }
            repeat(12) { deck.add(Maki2) }
            repeat(8) { deck.add(Maki3) }
            repeat(14) { deck.add(Tempura) }
            repeat(14) { deck.add(Sashimi) }
            repeat(14) { deck.add(Dumplings) }
            repeat(10) { deck.add(Pudding) }
            repeat(4) { deck.add(Chopsticks) }

            deck.shuffle()
        }
    }
}