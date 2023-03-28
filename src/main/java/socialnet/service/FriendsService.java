package socialnet.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import socialnet.api.friends.*;
import socialnet.dto.PersonRs;
import socialnet.model.Friendships;
import socialnet.model.Person;
import socialnet.model.enums.FriendshipStatusTypes;
import socialnet.repository.friends.FriendsShipsRepository;
import socialnet.repository.friends.PersonRepositoryFriends;
import socialnet.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static socialnet.model.enums.FriendshipStatusTypes.BLOCKED;

public class FriendsService {

    private JwtUtils jwtUtils;
    private PersonRepositoryFriends personRepository;

    private FriendsShipsRepository friendsShipsRepository;

    public FriendsService(JwtUtils jwtUtils,
                          PersonRepositoryFriends personRepository,
                          FriendsShipsRepository friendsShipsRepository) {
        this.jwtUtils = jwtUtils;
        this.personRepository = personRepository;
        this.friendsShipsRepository = friendsShipsRepository;
    }

    public ResponseEntity<?> getFriendsUsing(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserNameFromJwtToken(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Friendships> allFriendships = friendsShipsRepository.findAllFriendships(personsEmail.get(0).getId());
            if (allFriendships.isEmpty()){
                return new ResponseEntity<>(new CommonRsListPersonRs(), HttpStatus.OK);
            } else {
                List<Long> friendsId = new ArrayList<>();
                allFriendships.forEach((Friendships friendship) -> {
                    if (!friendsId.contains(friendship.getSrcPersonId())){friendsId.add(friendship.getSrcPersonId());}
                    if (!friendsId.contains(friendship.getDstPersonId())){friendsId.add(friendship.getDstPersonId());}
                });
                String friendsIdString = friendsIdStringMethod(friendsId);
                List<Person> personList = personRepository.findPersonFriendsAll(friendsIdString);
                return new ResponseEntity<>(fillingCommonRsListPersonRs(personList, offset, perPage), HttpStatus.OK);
            }
        }
    }

    private String friendsIdStringMethod(List<Long> friendsId) {
        String friendsIdString = "";
        for (int i = 0; i < friendsId.size(); i++){
            if (i < friendsId.size() - 1) {
                friendsIdString = friendsIdString + " id=" + friendsId.get(i).toString() + " OR ";
            } else {
                friendsIdString = friendsIdString + " id=" + friendsId.get(i).toString();
            }
        }
        return friendsIdString;
    }

    public CommonRsListPersonRs fillingCommonRsListPersonRs(List<Person> personList, Integer offset, Integer perPage){
        CommonRsListPersonRs commonRsListPersonRsFilling = new CommonRsListPersonRs();
        List<Person> personListOffset = new ArrayList<>();
        for (int i = offset; i < offset + perPage; i++){
            personListOffset.add(personList.get(i));
        }
        List<PersonRs> personRsList = createPersonRsList(personListOffset);
        commonRsListPersonRsFilling.setData(personRsList);
        Date date = new Date();
        commonRsListPersonRsFilling.setTimestamp(date.getTime());
        commonRsListPersonRsFilling.setTotal((long) personList.size());
        commonRsListPersonRsFilling.setPerPage(personListOffset.size());
        commonRsListPersonRsFilling.setItemPerPage(personListOffset.size());
        return commonRsListPersonRsFilling;
    }
    public List<PersonRs> createPersonRsList(List<Person> personList){
        List<PersonRs> personRsList= new ArrayList<>();
        for (Person person : personList) {
            PersonRs rs = new PersonRs();
            rs.setAbout(person.getAbout());
            rs.setBirthDate(person.getBirthDate().toString());
            rs.setCity(person.getCity());
            rs.setCountry(person.getCountry());
            rs.setCurrency(null);
            rs.setEmail(person.getEmail());
            rs.setFirstName(person.getFirstName());
            rs.setFriendStatus("FRIEND");
            rs.setId(person.getId());
            rs.setIsBlocked(person.getIsBlocked());
            rs.setIsBlockedByCurrentUser(false);
            rs.setLastOnlineTime(person.getLastOnlineTime().toString());
            rs.setMessagesPermission(null);
            if (person.getOnlineStatus().equals(true)){
                rs.setOnline(true);
            } else {
                rs.setOnline(false);
            }
            rs.setPhone(person.getPhone());
            rs.setPhoto(person.getPhoto());
            rs.setRegDate(person.getRegDate().toString());
            rs.setToken(null);
            rs.setUserDeleted(person.getIsDeleted());
            rs.setWeather(null);
            personRsList.add(rs);
        }
        return personRsList;
    }


    public ResponseEntity<?> userBlocksUserUsingPOST(String authorization, Integer id) {
        String email = jwtUtils.getUserNameFromJwtToken(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Friendships> friend = friendsShipsRepository.findFriend(personsEmail.get(0).getId(), id);
            if (friend.isEmpty()){
                return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'friend' is empty"),
                    HttpStatus.BAD_REQUEST);
            } else {
                FriendshipStatusTypes status = friend.get(0).getStatusName();
                Long idFriend = friend.get(0).getId();
                String statusNew = "";
                if (friend.get(0).getStatusName().equals(BLOCKED)){
                    statusNew = "FRIEND";
                } else {
                    statusNew = "BLOCKED";
                }
                friendsShipsRepository.insertStatusFriend(idFriend, statusNew);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
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

    public ResponseEntity<?> getOutgoingRequestsUsingGET(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserNameFromJwtToken(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Friendships> outgoingRequests = friendsShipsRepository
                    .findAllOutgoingRequests(personsEmail.get(0).getId());
            if (outgoingRequests.isEmpty()){
                return new ResponseEntity<>(errorRs("EmptyEmailException",
                        "Field 'OutgoingRequests' is empty"), HttpStatus.BAD_REQUEST);
            } else {
                List<Long> requestsId = new ArrayList<>();
                outgoingRequests.forEach((Friendships friendship) -> {
                    requestsId.add(friendship.getId());
                });
                String friendsIdString = friendsIdStringMethod(requestsId);
                List<Person> personList = personRepository.findPersonFriendsAll(friendsIdString);
                return new ResponseEntity<>(fillingCommonRsListPersonRs(personList, offset, perPage), HttpStatus.OK);
            }
        }
    }

    public ResponseEntity<?> getRecommendedFriendsUsingGET(String authorization) {
        return new ResponseEntity<>( HttpStatus.OK);
    }


    public ResponseEntity<?> getPotentialFriendsUsingGET(String authorization, Integer orElse, Integer orElse1) {
        return new ResponseEntity<>( HttpStatus.OK);
    }


    public ResponseEntity<?> addFriendUsingPOST(String authorization, Integer id) {
        String email = jwtUtils.getUserNameFromJwtToken(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
//            friendsShipsRepository.addFriend(time, id, idFriend, status);
//            ComplexRs complexRs = new C;
//
//            return new ResponseEntity<>(fillingCommonRsListPersonRs(personList, offset, perPage), HttpStatus.OK);
        }
        return new ResponseEntity<>( HttpStatus.OK);
    }
////////////////////////////////////////



    public ResponseEntity<?> deleteSentFriendshipRequestUsingDELETE(String authorization, Integer id) {
        return new ResponseEntity<>( HttpStatus.OK);
    }


    public ResponseEntity<?> sendFriendshipRequestUsingPOST(String authorization, Integer id) {
        return new ResponseEntity<>( HttpStatus.OK);
    }


    public ResponseEntity<?> deleteFriendUsingDELETE(String authorization, Integer id) {
        return new ResponseEntity<>( HttpStatus.OK);
    }
}
