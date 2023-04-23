package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import socialnet.api.response.NotificationRs;
import socialnet.api.response.PersonRs;
import socialnet.model.Notification;
@Mapper(componentModel = "spring",imports = NotificationMapper.class)
public interface NotificationMapper {

    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mapping(source = "notification.id",target = "id")
    @Mapping(source = "notification.sentTime",target = "sentTime")
    @Mapping(source = "notification.notificationType",target = "notificationType")
    @Mapping(source = "personRs",target = "entityAuthor")
    NotificationRs toDTO (Notification notification, PersonRs personRs);
}
