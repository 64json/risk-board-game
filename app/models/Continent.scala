package models

import models.interface.{Formattable, Identifiable}

class Continent(val name: String, val territories: List[Territory]) extends Identifiable {
  def apply(number: Int): Territory = territories(number - 1)

  override def fields = Map(
    "id" -> id,
    "name" -> name,
    "territories" -> territories,
  )
}

object Continent {
  def createContinents: List[Continent] = {
    // Refer to https://en.wikipedia.org/wiki/Risk_(game)#/media/File:Risk_game_map_fixed.png
    val NORTH_AMERICA = new Continent("Great Cities", List(
      "San Fransokyo",
      "Hawaii",
      "New Orleans",
      "The Bayeux",
      "Notre Dame",
      "Court of Miracles",
      "Arendelle",
      "Elsa's Castle",
      "London",
    ).map(name => new Territory(name)))

    val SOUTH_AMERICA = new Continent("Lands Unknown", List(
      "Cusco Castle",
      "Pacha's House",
      "Yzma's Lair",
      "Neverland",
    ).map(name => new Territory(name)))

    val EUROPE = new Continent("The Realm of Warriors", List(
      "Great Wall of China",
      "Imperial City",
      "Agrabah",
      "The Cave of Wonders",
      "Athens",
      "Mount Olympus",
      "The Underworld",
    ).map(name => new Territory(name)))

    val AFRICA = new Continent("Land of the Fittest", List(
      "The Pridelands",
      "The Elephant Graveyard",
      "Timon and Pumba's Hideout",
      "Gaston's Tavern",
      "Beast's Castle",
      "Belle's Cottage",
    ).map(name => new Territory(name)))

    val ASIA = new Continent("The Realm of Princesses", List(
      "Evil Queen's Castle",
      "Dwarves' Cottage",
      "Maleficent's Castle",
      "Fairies' Cottage",
      "Charming Palace",
      "Cinderella's House",
      "Rapunzel's Tower",
      "Corona",
      "Atlantis",
      "Atlantica",
      "Ariel's Grotto",
      "Eric's Castle",
    ).map(name => new Territory(name)))

    val AUSTRALIA = new Continent("Wayfinders' Islands", List(
      "Motonui",
      "Te Fiti",
      "Maui's Island",
      "Realm of Monsters",
    ).map(name => new Territory(name)))

    //TODO: comment after every single border what EACH TERRITORY is actually named
    //indexed from 1...for some reason.

    NORTH_AMERICA(1).border( //San Fransokyo Borders:
      NORTH_AMERICA(6),      //Court of Miracles
      NORTH_AMERICA(2),     //Hawaii
      ASIA(6),               //Cinderella's House
    )
    NORTH_AMERICA(2).border( //Hawaii's Borders:
      NORTH_AMERICA(6),      //Court of Miracles
      NORTH_AMERICA(7),     //Arendelle
      NORTH_AMERICA(9),     //London
      NORTH_AMERICA(1),     //San Fransokyo
    )
    NORTH_AMERICA(3).border( //New Orleans' Borders:
      NORTH_AMERICA(9),     //London
      NORTH_AMERICA(4),     //The Bayeux
      SOUTH_AMERICA(4),     //Neverland
    )
    NORTH_AMERICA(4).border( //The Bayeux's Borders:
      NORTH_AMERICA(7),      //Arendelle
      NORTH_AMERICA(8),      //Elsa's Castle
      NORTH_AMERICA(3),      //New Orleans
      NORTH_AMERICA(9),      //London
    )
    NORTH_AMERICA(5).border( //Notre Dame's Borders:
      EUROPE(2),            //Imperial City
      NORTH_AMERICA(8),     //Elsa's Castle
      NORTH_AMERICA(7),     //Arendelle
      NORTH_AMERICA(6),     //Court of Miracles
    )
    NORTH_AMERICA(6).border( //Court of Miracles' Borders:
      NORTH_AMERICA(5),     //Notre Dame
      NORTH_AMERICA(7),     //Arendelle
      NORTH_AMERICA(2),     //Hawaii
      NORTH_AMERICA(1),     //San Fransokyo
    )
    NORTH_AMERICA(7).border( //Arendelle's Borders
      NORTH_AMERICA(6),     //Court of Miracles
      NORTH_AMERICA(5),     //Notre Dame
      NORTH_AMERICA(8),     //Elsa's Castle
      NORTH_AMERICA(4),     //The Bayeux
      NORTH_AMERICA(9),     //London
      NORTH_AMERICA(2),     //Hawaii
    )
    NORTH_AMERICA(8).border( //Elsa's Castle's Borders:
      NORTH_AMERICA(5),     //Notre Dame
      NORTH_AMERICA(4),     //The Bayeux
      NORTH_AMERICA(7),     //Arendelle
    )
    NORTH_AMERICA(9).border( //London's Borders:
      NORTH_AMERICA(2),     //Hawaii
      NORTH_AMERICA(7),     //Arendelle
      NORTH_AMERICA(4),     //The Bayeux
      NORTH_AMERICA(3),     //New Orleans
    )

    SOUTH_AMERICA(1).border( //Kuzco Castle Borders:
      SOUTH_AMERICA(3),      //Yzma's Lair
      SOUTH_AMERICA(2),      //Pacha's House
    )
    SOUTH_AMERICA(2).border( //Pacha's House's Borders:
      SOUTH_AMERICA(4),     //Neverland
      AFRICA(5),            //Beast's Castle
      SOUTH_AMERICA(1),     //Kuzco's Castle
      SOUTH_AMERICA(3),     //Yzma's Lair
    )
    SOUTH_AMERICA(3).border( //Yzma's Lair's Borders:
      SOUTH_AMERICA(4),     //Nerverland
      SOUTH_AMERICA(2),     //Pacha's House
      SOUTH_AMERICA(1),     //Kuzco's Castle
    )
    SOUTH_AMERICA(4).border( //Neverland's Borders
      NORTH_AMERICA(3),     //New Orleans
      SOUTH_AMERICA(2),     //Pacha's House
      SOUTH_AMERICA(3),     //Yzma's Lair
    )

    EUROPE(1).border(      //Great Wall of China's Borders:
      EUROPE(2),          //Imperial City
      EUROPE(4),          //The Cave of Wonders
      EUROPE(3),          //Agrabah
      EUROPE(7),          //The Underworld
    )
    EUROPE(2).border(     //Imperial City's Borders:
      NORTH_AMERICA(5),   //Notre Dame
      EUROPE(4),          //The Cave of Wonders
      EUROPE(1),          //Great Wall of China
    )
    EUROPE(3).border(     //Agrabah's Borders:
      EUROPE(4),          //The Cave of Wonders
      EUROPE(6),          //Mount Olympus
      EUROPE(5),          //Athens
      EUROPE(7),          //The Underworld
      EUROPE(1),          //Great Wall of China
    )
    EUROPE(4).border(     //The Cave of Wonders' Borders:
      EUROPE(6),          //Mount Olympus
      EUROPE(3),          //Agrabah
      EUROPE(1),          //Great Wall of China
      EUROPE(2),          //Imperial City
    )
    EUROPE(5).border(     //Athens' Borders:
      EUROPE(3),          //Agrabah
      EUROPE(6),          //Mount Olympus
      ASIA(7),            //Rapunzel's Tower
      AFRICA(3),          //Timon and Pumba's Hideout
      AFRICA(5),          //Beast's Castle
      EUROPE(7),          //The Underworld
    )
    EUROPE(6).border(     //Mount Olympus' Borders:
      EUROPE(4),          //The Cave of Wonders
      ASIA(11),           //Ariel's Grotto
      ASIA(1),            //Evil Queen's Castle
      ASIA(7),            //Rapunzel's Tower
      EUROPE(5),          //Athens
      EUROPE(3),          //Agrabah
    )
    EUROPE(7).border(     //The Underworld's Borders:
      EUROPE(1),          //Great Wall of China
      EUROPE(3),          //Agrabah
      EUROPE(5),          //Athens
      AFRICA(5),          //Beast's Castle
    )

    AFRICA(1).border(     //The Pridelands' Borders:
      AFRICA(5),          //Beast's Castle
      AFRICA(2),          //The Elephant Graveyard
      AFRICA(6),          //Belle's Cottage
    )
    AFRICA(2).border(     //The Elephant Graveyard's Borders:
      AFRICA(3),          //Timon and Pumba's Hideout
      ASIA(7),            //Rapunzel's Tower
      AFRICA(4),          //Gaston's Tavern
      AFRICA(6),          //Belle's Cottage
      AFRICA(1),          //The Pridelands
      AFRICA(5),          //Beast's Castle
    )
    AFRICA(3).border(     //Timon and Pumba's Hideout Borders:
      EUROPE(5),          //Athens
      ASIA(7),            //Rapunzel's Tower
      AFRICA(2),          //The Elephant Graveyard
      AFRICA(5),          //Beast's Castle
    )
    AFRICA(4).border(     //Gaston's Tavern Borders:
      AFRICA(2),          //The Elephant Graveyard
      AFRICA(6),          //Belle's Cottage
    )
    AFRICA(5).border(     //Beast's Castle Borders:
      EUROPE(7),          //The Underworld
      EUROPE(5),          //Athens
      AFRICA(3),          //Timon and Pumba's Hideout
      AFRICA(2),          //The Elephant Graveyard
      AFRICA(1),          //The Pridelands
      SOUTH_AMERICA(2),   //Pacha's House
    )
    AFRICA(6).border(     //Belle's Cottage Borders:
      AFRICA(1),          //The Pridelands
      AFRICA(2),          //The Elephant Graveyard
      AFRICA(4),          //Gaston's Tavern
    )

    ASIA(1).border(
      EUROPE(6),
      ASIA(11),
      ASIA(2),
      ASIA(3),
      ASIA(7),
    )
    ASIA(2).border(
      ASIA(8),
      ASIA(9),
      ASIA(3),
      ASIA(1),
      ASIA(11),
      ASIA(10),
    )
    ASIA(3).border(
      ASIA(1),
      ASIA(2),
      ASIA(9),
      ASIA(7),
    )
    ASIA(4).border(
      ASIA(12),
      ASIA(6),
      ASIA(8),
      ASIA(10),
    )
    ASIA(5).border(
      ASIA(6),
      ASIA(8),
    )
    ASIA(6).border(
      ASIA(12),
      NORTH_AMERICA(1),
      ASIA(5),
      ASIA(8),
      ASIA(4),
    )
    ASIA(7).border(
      EUROPE(6),
      ASIA(1),
      ASIA(3),
      AFRICA(2),
      AFRICA(3),
      EUROPE(5),
    )
    ASIA(8).border(
      ASIA(4),
      ASIA(6),
      ASIA(5),
      ASIA(2),
      ASIA(10),
    )
    ASIA(9).border(
      ASIA(2),
      AUSTRALIA(2),
      ASIA(3),
    )
    ASIA(10).border(
      ASIA(12),
      ASIA(4),
      ASIA(8),
      ASIA(2),
      ASIA(11),
    )
    ASIA(11).border(
      ASIA(10),
      ASIA(2),
      ASIA(1),
      EUROPE(6),
    )
    ASIA(12).border(
      ASIA(6),
      ASIA(4),
      ASIA(10),
    )

    AUSTRALIA(1).border(
      AUSTRALIA(3),
      AUSTRALIA(4),
    )
    AUSTRALIA(2).border(
      ASIA(9),
      AUSTRALIA(3),
      AUSTRALIA(4),
    )
    AUSTRALIA(3).border(
      AUSTRALIA(1),
      AUSTRALIA(4),
      AUSTRALIA(2),
    )
    AUSTRALIA(4).border(
      AUSTRALIA(2),
      AUSTRALIA(3),
      AUSTRALIA(1),
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