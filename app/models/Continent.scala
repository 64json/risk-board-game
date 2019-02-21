package models

class Continent(val name: String, val territories: List[Territory]) extends Identifiable {
  def apply(number: Int): Territory = territories(number - 1)
}

object Continent {
  def createContinents: List[Continent] = {
    // Refer to https://en.wikipedia.org/wiki/Risk_(game)#/media/File:Risk_game_map_fixed.png
    val NORTH_AMERICA = new Continent("North America", List(
      "Alaska",
      "Alberta",
      "Central America",
      "Eastern United States",
      "Greenland",
      "Northwest Territory",
      "Ontario",
      "Quebec",
      "Western United States",
    ).map(name => new Territory(name)))

    val SOUTH_AMERICA = new Continent("South America", List(
      "Argentina",
      "Brazil",
      "Peru",
      "Venezuela",
    ).map(name => new Territory(name)))

    val EUROPE = new Continent("Europe", List(
      "Great Britain",
      "Iceland",
      "Northern Europe",
      "Scandinavia",
      "Southern Europe",
      "Ukraine",
      "Western Europe",
    ).map(name => new Territory(name)))

    val AFRICA = new Continent("Africa", List(
      "Congo",
      "East Africa",
      "Egypt",
      "Madagascar",
      "North Africa",
      "South Africa",
    ).map(name => new Territory(name)))

    val ASIA = new Continent("Asia", List(
      "Afghanistan",
      "China",
      "India",
      "Irkutsk",
      "Japan",
      "Kamchatka",
      "Middle East",
      "Mongolia",
      "Siam",
      "Siberia",
      "Ural",
      "Yakutsk",
    ).map(name => new Territory(name)))

    val AUSTRALIA = new Continent("Australia", List(
      "Eastern Australia",
      "Indonesia",
      "New Guinea",
      "Western Australia",
    ).map(name => new Territory(name)))

    NORTH_AMERICA(1).border(
      NORTH_AMERICA(6),
      NORTH_AMERICA(2),
      ASIA(6),
    )
    NORTH_AMERICA(2).border(
      NORTH_AMERICA(6),
      NORTH_AMERICA(7),
      NORTH_AMERICA(9),
      NORTH_AMERICA(1),
    )
    NORTH_AMERICA(3).border(
      NORTH_AMERICA(9),
      NORTH_AMERICA(4),
      SOUTH_AMERICA(4),
    )
    NORTH_AMERICA(4).border(
      NORTH_AMERICA(7),
      NORTH_AMERICA(8),
      NORTH_AMERICA(3),
      NORTH_AMERICA(9),
    )
    NORTH_AMERICA(5).border(
      EUROPE(2),
      NORTH_AMERICA(8),
      NORTH_AMERICA(7),
      NORTH_AMERICA(6),
    )
    NORTH_AMERICA(6).border(
      NORTH_AMERICA(5),
      NORTH_AMERICA(7),
      NORTH_AMERICA(2),
      NORTH_AMERICA(1),
    )
    NORTH_AMERICA(7).border(
      NORTH_AMERICA(6),
      NORTH_AMERICA(5),
      NORTH_AMERICA(8),
      NORTH_AMERICA(4),
      NORTH_AMERICA(9),
      NORTH_AMERICA(2),
    )
    NORTH_AMERICA(8).border(
      NORTH_AMERICA(5),
      NORTH_AMERICA(4),
      NORTH_AMERICA(7),
    )
    NORTH_AMERICA(9).border(
      NORTH_AMERICA(2),
      NORTH_AMERICA(7),
      NORTH_AMERICA(4),
      NORTH_AMERICA(3),
    )

    SOUTH_AMERICA(1).border(
      SOUTH_AMERICA(3),
      SOUTH_AMERICA(2),
    )
    SOUTH_AMERICA(2).border(
      SOUTH_AMERICA(4),
      AFRICA(5),
      SOUTH_AMERICA(1),
      SOUTH_AMERICA(3),
    )
    SOUTH_AMERICA(3).border(
      SOUTH_AMERICA(4),
      SOUTH_AMERICA(2),
      SOUTH_AMERICA(1),
    )
    SOUTH_AMERICA(4).border(
      NORTH_AMERICA(3),
      SOUTH_AMERICA(2),
      SOUTH_AMERICA(3),
    )

    EUROPE(1).border(
      EUROPE(2),
      EUROPE(4),
      EUROPE(3),
      EUROPE(7),
    )
    EUROPE(2).border(
      NORTH_AMERICA(5),
      EUROPE(4),
      EUROPE(1),
    )
    EUROPE(3).border(
      EUROPE(4),
      EUROPE(6),
      EUROPE(5),
      EUROPE(7),
      EUROPE(1),
    )
    EUROPE(4).border(
      EUROPE(6),
      EUROPE(3),
      EUROPE(1),
      EUROPE(2),
    )
    EUROPE(5).border(
      EUROPE(3),
      EUROPE(6),
      ASIA(7),
      AFRICA(3),
      AFRICA(5),
      EUROPE(7),
    )
    EUROPE(6).border(
      EUROPE(4),
      ASIA(11),
      ASIA(1),
      ASIA(7),
      EUROPE(5),
      EUROPE(3),
    )
    EUROPE(7).border(
      EUROPE(1),
      EUROPE(3),
      EUROPE(5),
      AFRICA(5),
    )

    AFRICA(1).border(
      AFRICA(5),
      AFRICA(2),
      AFRICA(6),
    )
    AFRICA(2).border(
      AFRICA(3),
      ASIA(7),
      AFRICA(4),
      AFRICA(6),
      AFRICA(1),
      AFRICA(5),
    )
    AFRICA(3).border(
      EUROPE(5),
      ASIA(7),
      AFRICA(2),
      AFRICA(5),
    )
    AFRICA(4).border(
      AFRICA(2),
      AFRICA(6),
    )
    AFRICA(5).border(
      EUROPE(7),
      EUROPE(5),
      AFRICA(3),
      AFRICA(2),
      AFRICA(1),
      SOUTH_AMERICA(2),
    )
    AFRICA(6).border(
      AFRICA(1),
      AFRICA(2),
      AFRICA(4),
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