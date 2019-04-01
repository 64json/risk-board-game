package models

import models.interface.Identifiable

class Continent(val name: String, val additionalArmies: Int, val territories: List[Territory]) extends Identifiable {
  def apply(number: Int): Territory = territories(number - 1)

  override def fields = Map(
    "id" -> id,
    "name" -> name,
    "territories" -> territories,
  )

  def getTerritories: List[Territory] = territories
}

object Continent {
  def createContinents: List[Continent] = {
    // Refer to https://en.wikipedia.org/wiki/Risk_(game)#/media/File:Risk_game_map_fixed.png
    val NORTH_AMERICA = new Continent("Great Cities", 5, List(
      "Elsa's Castle",
      "Court of Miracles",
      "London",
      "The Bayeux",
      "San Fransokyo",
      "Arendelle",
      "Notre Dame",
      "Hawaii",
      "New Orleans",
    ).map(name => new Territory(name)))

    val SOUTH_AMERICA = new Continent("Lands Unknown", 2, List(
      "Yzma's House",
      "Kuzco Castle",
      "Pacha's House",
      "Neverland",
    ).map(name => new Territory(name)))

    val EUROPE = new Continent("The Realm of Warriors", 5, List(
      "Imperial City",
      "Great Wall of China",
      "Mount Olympus",
      "The Cave of Wonders",
      "Athens",
      "Agrabah",
      "The Underworld",
    ).map(name => new Territory(name)))

    val AFRICA = new Continent("Land of the Fittest", 3, List(
      "The Elephant Graveyard",
      "Beast's Castle",
      "Timon and Pumba's Hideout",
      "Belle's Cottage",
      "The Pridelands",
      "Gaston's Tavern",
    ).map(name => new Territory(name)))

    val ASIA = new Continent("The Realm of Princesses", 7, List(
      "Cinderella's House",
      "Fairies' Cottage",
      "Maleficent's Castle",
      "Evil Queen's Castle",
      "Ariel's Grotto",
      "Eric's Castle",
      "Charming Palace",
      "Atlantica",
      "Atlantis",
      "Rapunzel's Tower",
      "Corona",
      "Dwarves' Cottage",
    ).map(name => new Territory(name)))

    val AUSTRALIA = new Continent("Wayfinders' Islands", 2, List(
      "Realm of Monsters",
      "Motonui",
      "Te Fiti",
      "Maui's Island",
    ).map(name => new Territory(name)))

    NORTH_AMERICA(1).border( //Elsa's Castle Borders:
      NORTH_AMERICA(6), //Arendelle
      NORTH_AMERICA(2), //Court of Miracles
      ASIA(6), //Eric's Castle
    )
    NORTH_AMERICA(2).border( //Court of Miracles' Borders:
      NORTH_AMERICA(6), //Arendelle
      NORTH_AMERICA(7), //Notre Dame
      NORTH_AMERICA(9), //New Orleans
      NORTH_AMERICA(1), //Elsa's Castle
    )
    NORTH_AMERICA(3).border( //London' Borders:
      NORTH_AMERICA(9), //New Orleans
      NORTH_AMERICA(4), //The Bayeux
      SOUTH_AMERICA(4), //Neverland
    )
    NORTH_AMERICA(4).border( //The Bayeux's Borders:
      NORTH_AMERICA(7), //Notre Dame
      NORTH_AMERICA(8), //Hawaii
      NORTH_AMERICA(3), //London
      NORTH_AMERICA(9), //New Orleans
    )
    NORTH_AMERICA(5).border( //San Fransokyo's Borders:
      EUROPE(2), //Great Wall of China
      NORTH_AMERICA(8), //Hawaii
      NORTH_AMERICA(7), //Notre Dame
      NORTH_AMERICA(6), //Arendelle
    )
    NORTH_AMERICA(6).border( //Arendelle's Borders:
      NORTH_AMERICA(5), //San Fransokyo
      NORTH_AMERICA(7), //Notre Dame
      NORTH_AMERICA(2), //Court of Miracles
      NORTH_AMERICA(1), //Elsa's Castle
    )
    NORTH_AMERICA(7).border( //Notre Dame's Borders
      NORTH_AMERICA(6), //Arendelle
      NORTH_AMERICA(5), //San Fransokyo
      NORTH_AMERICA(8), //Hawaii
      NORTH_AMERICA(4), //The Bayeux
      NORTH_AMERICA(9), //New Orleans
      NORTH_AMERICA(2), //Court of Miracles
    )
    NORTH_AMERICA(8).border( //Hawaii's Borders:
      NORTH_AMERICA(5), //San Fransokyo
      NORTH_AMERICA(4), //The Bayeux
      NORTH_AMERICA(7), //Notre Dame
    )
    NORTH_AMERICA(9).border( //New Orleans' Borders:
      NORTH_AMERICA(2), //Court of Miracles
      NORTH_AMERICA(7), //Notre Dame
      NORTH_AMERICA(4), //The Bayeux
      NORTH_AMERICA(3), //London
    )

    SOUTH_AMERICA(1).border( //Yzma's Lair's Borders:
      SOUTH_AMERICA(3), //Pacha's House
      SOUTH_AMERICA(2), //Kuzco's Castle
    )
    SOUTH_AMERICA(2).border( //Kuzco's Castle's Borders:
      SOUTH_AMERICA(4), //Neverland
      AFRICA(5), //The Pridelands
      SOUTH_AMERICA(1), //Yzma's Lair
      SOUTH_AMERICA(3), //Pacha's House
    )
    SOUTH_AMERICA(3).border( //Pacha's House's Borders:
      SOUTH_AMERICA(4), //Nerverland
      SOUTH_AMERICA(2), //Kuzco's Caslte
      SOUTH_AMERICA(1), //Yzma's Lair
    )
    SOUTH_AMERICA(4).border( //Neverland's Borders
      NORTH_AMERICA(3), //London
      SOUTH_AMERICA(2), //Kuzco's Castle
      SOUTH_AMERICA(3), //Pacha's House
    )

    EUROPE(1).border( //Imperial City's Borders:
      EUROPE(2), //Great Wall of China
      EUROPE(4), //The Cave of Wonders
      EUROPE(3), //Mount Olympus
      EUROPE(7), //The Underworld
    )
    EUROPE(2).border( //Great Wall of China's Borders:
      NORTH_AMERICA(5), //San Fransokyo
      EUROPE(4), //The Cave of Wonders
      EUROPE(1), //Imperial City
    )
    EUROPE(3).border( //Mount Olympus' Borders:
      EUROPE(4), //The Cave of Wonders
      EUROPE(6), //Agrabah
      EUROPE(5), //Athens
      EUROPE(7), //The Underworld
      EUROPE(1), //Imperial City
    )
    EUROPE(4).border( //The Cave of Wonders' Borders:
      EUROPE(6), //Agrabah
      EUROPE(3), //Mount Olympus
      EUROPE(1), //Imperial City
      EUROPE(2), //Great Wall of China
    )
    EUROPE(5).border( //Athens' Borders:
      EUROPE(3), //Mount Olympus
      EUROPE(6), //Agrabah
      ASIA(7), //Charming Palace
      AFRICA(3), //Timon and Pumba's Hideout
      AFRICA(5), //The Pridelands
      EUROPE(7), //The Underworld
    )
    EUROPE(6).border( //Agrabah's Borders:
      EUROPE(4), //The Cave of Wonders
      ASIA(11), //Corona
      ASIA(1), //Cinderella's House
      ASIA(7), //Charming Palace
      EUROPE(5), //Athens
      EUROPE(3), //Mount Olympus
    )
    EUROPE(7).border( //The Underworld's Borders:
      EUROPE(1), //Imperial City
      EUROPE(3), //Mount Olympus
      EUROPE(5), //Athens
      AFRICA(5), //The Pridelands
    )

    AFRICA(1).border( //The Elephant Graveyard's Borders:
      AFRICA(5), //The Pridelands
      AFRICA(2), //Beast's Castle
      AFRICA(6), //Gaston's Tavern
    )
    AFRICA(2).border( //Beast's Castle Borders:
      AFRICA(3), //Timon and Pumba's Hideout
      ASIA(7), //Charming Palace
      AFRICA(4), //Belle's Cottage
      AFRICA(6), //Gaston's Tavern
      AFRICA(1), //The Elephant Graveyard
      AFRICA(5), //The Pridelands
    )
    AFRICA(3).border( //Timon and Pumba's Hideout Borders:
      EUROPE(5), //Athens
      ASIA(7), //Charming Palace
      AFRICA(2), //Beast's Castle
      AFRICA(5), //The Pridelands
    )
    AFRICA(4).border( //Belle's Cottage Borders:
      AFRICA(2), //Beast's Castle
      AFRICA(6), //Gaston's Tavern
    )
    AFRICA(5).border( //The Pridelands Borders:
      EUROPE(7), //The Underworld
      EUROPE(5), //Athens
      AFRICA(3), //Timon and Pumba's Hideout
      AFRICA(2), //Beast's Castle
      AFRICA(1), //The Elephant Graveyard
      SOUTH_AMERICA(2), //Kuzco Castle
    )
    AFRICA(6).border( //Gaston's Tavern Borders:
      AFRICA(1), //The Elephant Graveyard
      AFRICA(2), //The Pridelands
      AFRICA(4), //Belle's Cottage
    )

    ASIA(1).border( //Cinderella's House's Borders:
      EUROPE(6), //Agrabah
      ASIA(11), //Corona
      ASIA(2), //Fairies' Cottage
      ASIA(3), //Maleficent's Castle
      ASIA(7), //Charming Palace
    )
    ASIA(2).border( //Fairies' Cottage's Borders:
      ASIA(8), //Atlantica
      ASIA(9), //Atlantis
      ASIA(3), //Maleficent's Castle
      ASIA(1), //Cinderella's House
      ASIA(11), //Corona
      ASIA(10), //Rapunzel's Tower
    )
    ASIA(3).border( //Maleficent's Castle's Borders:
      ASIA(1), //Cinderella's House
      ASIA(2), //Fairies' Cottage
      ASIA(9), //Atlantis
      ASIA(7), //Charming Palace
    )
    ASIA(4).border( //Evil Queen's Castle's Borders:
      ASIA(12), //Dwarves' Cottage
      ASIA(6), //Eric's Castle
      ASIA(8), //Atlantica
      ASIA(10), //Rapunzel's Tower
    )
    ASIA(5).border( //Ariel's Grotto's Borders:
      ASIA(6), //Eric's Castle
      ASIA(8), //Atlantica
    )
    ASIA(6).border( //Eric's Castle's Borders:
      ASIA(12), //Dwarves' Cottage
      NORTH_AMERICA(1), //Elsa's Castle
      ASIA(5), //Ariel's Grotto
      ASIA(8), //Atlantica
      ASIA(4), //Evil Queen's Castle
    )
    ASIA(7).border( //Charming Palace's Borders:
      EUROPE(6), //Agrabah
      ASIA(1), //Cinderella's House
      ASIA(3), //Maleficent's Castle
      AFRICA(2), //Beast's Castle
      AFRICA(3), //Timon and Pumba's Hideout
      EUROPE(5), //Ariel's Grotto
    )
    ASIA(8).border( //Atlantica's Borders:
      ASIA(4), //Evil Queen's Castle
      ASIA(6), //Eric's Castle
      ASIA(5), //Ariel's Grotto
      ASIA(2), //Fairies' Cottage
      ASIA(10), //Rapunzel's Tower
    )
    ASIA(9).border( //Atlantis' Borders:
      ASIA(2), //Fairies' Cottage
      AUSTRALIA(2), //Motonui
      ASIA(3), //Maleficent's Castle
    )
    ASIA(10).border( //Rapunzel's Tower's Borders:
      ASIA(12), //Dwarves' Cottage
      ASIA(4), //Evil Queen's Castle
      ASIA(8), //Atlantica
      ASIA(2), //Fairies' Cottage
      ASIA(11), //Corona
    )
    ASIA(11).border( //Corona's Borders:
      ASIA(10), //Rapunzel's Tower
      ASIA(2), //Fairies' Cottage
      ASIA(1), //Cinderella's house
      EUROPE(6), //Agrabah
    )
    ASIA(12).border( //Dwarves' Cottage's Borders:
      ASIA(6), //Eric's Castle
      ASIA(4), //Evil Queen's Castle
      ASIA(10), //Rapunzel's Tower
    )

    AUSTRALIA(1).border( //Realm of Monsters' Borders:
      AUSTRALIA(3), //Te Fiti
      AUSTRALIA(4), //Maui's Island
    )
    AUSTRALIA(2).border( //Motonui's Borders:
      ASIA(9), //Atlantis
      AUSTRALIA(3), //Te Fiti
      AUSTRALIA(4), //Maui's Island
    )
    AUSTRALIA(3).border( //Te Fiti's Borders:
      AUSTRALIA(1), //Realm of Monsters
      AUSTRALIA(4), //Maui's Island
      AUSTRALIA(2), //Motonui
    )
    AUSTRALIA(4).border( //Maui's Island's Borders:
      AUSTRALIA(2), //Motonui
      AUSTRALIA(3), // Te Fiti
      AUSTRALIA(1), //Realm of Monsters
    )

    List(
      NORTH_AMERICA,
      SOUTH_AMERICA,
      EUROPE,
      AFRICA,
      ASIA,
      AUSTRALIA,
    )
  }
}