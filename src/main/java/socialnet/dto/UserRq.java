package socialnet.dto;

import lombok.Data;

@Data
public class UserRq {

      private String about;
      private String birthDate;
      private String city;
      private String country;
      private String firstName;
      private String lastName;
      private String messagesPermission;
      private String phone;
      private String photoId;
}
