package com.upwork.dynamodb;

import net.bytebuddy.utility.RandomString;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenUserSaved_thenRecordPersisted() {
        var user = Instancio.create(User.class);

        userRepository.save(user);

        assertThat(userRepository.findById(user.getId()))
            .isNotNull()
            .satisfies(retrievedUser -> {
                assertThat(retrievedUser.getName()).isEqualTo(user.getName());
                assertThat(retrievedUser.getEmail()).isEqualTo(user.getEmail());
            });
    }

    @Test
    void whenUserUpdated_thenPersistedRecordUpdated() {
        var user = Instancio.create(User.class);
        userRepository.save(user);

        var retrievedUser = userRepository.findById(user.getId());
        retrievedUser.setName(RandomString.make());
        userRepository.update(retrievedUser);

        assertThat(userRepository.findById(user.getId()))
            .isNotNull()
            .satisfies(updatedUser -> {
                assertThat(updatedUser.getName()).isNotEqualTo(user.getName());
                assertThat(updatedUser.getName()).isEqualTo(retrievedUser.getName());
            });
    }

    @Test
    void whenPersistedUserDeleted_thenRecordGetsDeleted() {
        var user = Instancio.create(User.class);
        userRepository.save(user);

        var retrievedUser = userRepository.findById(user.getId());
        assertThat(retrievedUser).isNotNull();

        userRepository.deleteById(user.getId());

        assertThrows(InvalidUserIdException.class, () ->
            userRepository.findById(user.getId())
        );
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void whenFindAllInvoked_thenAllPersistedRecordsReturned() {
        var count = 50;
        for (int i = 0; i < count; i++) {
            var user = Instancio.create(User.class);
            userRepository.save(user);
        }

        var retrievedUsers = userRepository.findAll();
        assertThat(retrievedUsers.size()).isEqualTo(count);
    }

    @Test
    void whenUserPersistedThenQueryableWithEmail() {
        var user = Instancio.create(User.class);
        userRepository.save(user);

        assertThat(userRepository.findByEmail(user.getEmail()))
            .isNotNull()
            .satisfies(retrievedUser -> {
                assertThat(retrievedUser.getId()).isEqualTo(user.getId());
                assertThat(retrievedUser.getName()).isEqualTo(user.getName());
                assertThat(retrievedUser.getEmail()).isEqualTo(user.getEmail());
            });

        assertThrows(InvalidUserEmailException.class, () ->
            userRepository.findByEmail(RandomString.make())
        );
    }

}