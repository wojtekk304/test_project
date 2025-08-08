import java.sql.*;

public class Main {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:h2:file:C:/Users/WojciechKarrakula/IdeaProjects/movie_database/data/mydbmovies";
        String username = "sa";
        String password = "hello";

        try (
                Connection conn = DriverManager.getConnection(jdbcURL, username, password);
                Statement stmt = conn.createStatement()
        ) {
            System.out.println("Connected to H2 Database.");

            createTable(stmt);
            int rowsLoaded = inputData(stmt);  // count of inserted/updated rows
            printMovies(stmt);

            System.out.printf("Loaded %d row%s.%n", rowsLoaded, rowsLoaded == 1 ? "" : "s");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Statement statement) throws SQLException {
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS movies (
                Film VARCHAR(100) UNIQUE,
                Genre VARCHAR(50),
                Lead_Studio VARCHAR(50),
                Audience_score_percentage INT,
                Profitability DOUBLE,
                Rotten_Tomatoes_Percentage INT,
                Worldwide_Gross DOUBLE,
                "Year" INT
            )
            """;
        statement.execute(createTableQuery);
        System.out.println("✅ Table 'movies' created.");
    }

    private static int inputData(Statement statement) throws SQLException {
        String insertQuery = """
            MERGE INTO movies (Film,Genre,Lead_Studio,Audience_score_percentage,Profitability,Rotten_Tomatoes_Percentage,Worldwide_Gross,"Year") KEY(Film)
            SELECT * FROM CSVREAD('movies.csv')
            """;
        return statement.executeUpdate(insertQuery);
    }

    private static void printMovies(Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM movies");
        int index = 1;
        while (rs.next()) {
            String film = rs.getString("Film");
            String genre = rs.getString("Genre");
            String leadStudio = rs.getString("Lead_Studio");
            int audienceScorePercent = rs.getInt("Audience_score_percentage");
            double profitability = rs.getDouble("Profitability");
            int rottenTomatoesPercent = rs.getInt("Rotten_Tomatoes_Percentage");
            double worldwideGross = rs.getDouble("Worldwide_Gross");
            int movieYear = rs.getInt("Year");

            System.out.printf(
                    "Movie #%d: %s | Genre: %s | Lead Studio: %s | Audience: %d%% | Profitability: %.2f | RT: %d%% | WW Gross: %.2f | Year: %d%n",
                    index++, film, genre, leadStudio, audienceScorePercent, profitability, rottenTomatoesPercent, worldwideGross, movieYear
            );
        }
    }
}
