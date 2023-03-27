package socialnet.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import socialnet.api.friends.CommonRsListPersonRs;
import socialnet.api.friends.ErrorRs;
import socialnet.api.friends.PersonRs;
import socialnet.model.Friendships;
import socialnet.model.Person;
import socialnet.repository.friends.FriendsShipsRepository;
import socialnet.repository.friends.PersonRepository;
import socialnet.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FriendsService {

    //JwtUtils jwtUtils;
    PersonRepository personRepository;

    FriendsShipsRepository friendsShipsRepository;

//    public FriendsService(JwtUtils jwtUtils,
//                          PersonRepository personRepository,
//                          FriendsShipsRepository friendsShipsRepository) {
//        this.jwtUtils = jwtUtils;
//        this.personRepository = personRepository;
//        this.friendsShipsRepository = friendsShipsRepository;
//    }

    public FriendsService(PersonRepository personRepository,
                          FriendsShipsRepository friendsShipsRepository) {
        this.personRepository = personRepository;
        this.friendsShipsRepository = friendsShipsRepository;
    }

    public ResponseEntity<?> getFriendsUsing(String authorization, Integer offset, Integer perPage) {
        CommonRsListPersonRs commonRsListPersonRs = new CommonRsListPersonRs();
        String email = "";
                //String email = jwtUtils.getUserNameFromJwtToken(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Friendships> allFriendships = friendsShipsRepository.findAllFriendships(personsEmail.get(0).getId());
            if (allFriendships.isEmpty()){
                return new ResponseEntity<>(commonRsListPersonRs, HttpStatus.OK);
            } else {
                List<Long> friendsId = new ArrayList<>();
                allFriendships.forEach((Friendships friendship) -> {
                    if (!friendsId.contains(friendship.getSrcPersonId())){friendsId.add(friendship.getSrcPersonId());}
                    if (!friendsId.contains(friendship.getDstPersonId())){friendsId.add(friendship.getDstPersonId());}
                });
                String friendsIdString ="";
                for (int i = 0; i < friendsId.size(); i++){
                    if (i < friendsId.size() - 1) {
                        friendsIdString = friendsIdString + " id=" + friendsId.get(i).toString() + " OR ";
                    } else {
                        friendsIdString = friendsIdString + " id=" + friendsId.get(i).toString();
                    }
                }
                List<Person> personList = personRepository.findPersonFriendsAll(friendsIdString);
                ///////////////////////
                List<PersonRs> personRsList= new ArrayList<>();
                for (Person person : personList) {
                    PersonRs rs = new PersonRs();
                    ///rs.s
                }
                ///commonRsListPersonRs.setData(personList);
                ///////////////////////
            }
        }
        return new ResponseEntity<>(commonRsListPersonRs, HttpStatus.OK);
    }


    public ResponseEntity<?> getFriendsBlockUnblock(String authorization, Integer id) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> getFriendsUsingId(String authorization, Integer id) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ErrorRs errorRs(String error, String description){
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError(error);
        errorRs.setError_description(description);
        Date date = new Date();
        errorRs.setTimestamp(date.getTime());
        return errorRs;
    }
}
