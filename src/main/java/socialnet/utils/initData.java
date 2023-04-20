package socialnet.utils;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;

@Component
public class initData {
    @Bean
    public CommandLineRunner commandLineRunner(CityParser cityParser,
                                               JdbcTemplate jdbcTemplate) {
        return args -> {
            if (args.length > 1) {
                switch (args[0].toLowerCase()) {
                    case "-city": {
                        /** -city RU */
                        if (args.length < 2) break;

                        URL url = new URL("https://bulk.openweathermap.org/sample/city.list.json.gz");
                        InputStream inputStream = url.openStream();

                         //TODO Переделать на Pipe InputStream - без сохранения в файл
                        (new GzipFile()).unGZipToFile(inputStream, url.toString().substring(url.toString().lastIndexOf("/") + 1));
                        //Files.copy(inputStream, new File("city.list.json.gz").toPath());

                        jdbcTemplate.update(cityParser.getInsertSqlCityByCountry("city.list.json", args[1].toUpperCase()));

//                        Path path = Paths.get("russian_cities.sql");
//                        byte[] stringToByte = sqlCities.getBytes();
//                        Files.write(path,stringToByte);
//                        System.out.println(sqlCities);
                        break;
                    }
                }

            }

            //countryRepository.initData();


            //Yandex cloud api
            //yandexCloudService.createKeyService();

//            var dbxClient = dropboxService.getClient("FileBox37");
//            var dbxName = dropboxService.getDisplayName(dbxClient);
//            List<String> fileList = dropboxService.getFilesList(dbxClient,"/photo");
//            fileList.forEach(System.out::println);



        };
    }

}
