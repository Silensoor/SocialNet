package socialnet.service;

import org.springframework.stereotype.Service;
import socialnet.dto.CurrencyRs;
import socialnet.dto.PersonRs;
import socialnet.dto.WeatherRs;
import socialnet.model.Person;

@Service
public class PersonService {
    public PersonRs convertToPersonRs(Person author) {
        PersonRs personRs = new PersonRs();
        personRs.setAbout(author.getAbout());
        personRs.setBirthDate(author.getBirthDate().toString());
        personRs.setCity(author.getCity());
        personRs.setCountry(author.getCountry());
        personRs.setCurrency(new CurrencyRs());
        personRs.setEmail(author.getEmail());
        personRs.setFirstName(author.getFirstName());
        personRs.setFriendStatus("friend_status");
        personRs.setId(author.getId());
        personRs.setIsBlocked(author.getIsBlocked());
        personRs.setIsBlockedByCurrentUser(false);
        personRs.setLastName(author.getLastName());
        personRs.setLastOnlineTime(author.getLastOnlineTime().toString());
        personRs.setMessagesPermission(author.getMessagePermissions());
        personRs.setOnline(true);
        personRs.setPhone(author.getPhone());
        personRs.setPhoto(author.getPhoto());
        personRs.setRegDate(author.getRegDate().toString());
        personRs.setToken(author.getChangePasswordToken());
        personRs.setUserDeleted(author.getIsDeleted());
        personRs.setWeather(new WeatherRs());

        return personRs;
    }
}
