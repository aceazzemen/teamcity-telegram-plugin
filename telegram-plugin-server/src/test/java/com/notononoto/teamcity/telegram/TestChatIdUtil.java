package com.notononoto.teamcity.telegram;

import jetbrains.buildServer.users.SUser;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TestChatIdUtil {

    private static List<Long> STANDARD_LIST = Arrays.asList(111L, 222L, 333L);
    private static String STANDARD_LIST_INPUT = "111;222;333";
    private static String RUBBISH_INPUT = "999;888;sdfghjkdcvbn";

    @Test
    public void we_can_extract_chat_ids_given_valid_input() {
        List<Long> chatIdExtracts = ChatIdUtil.extractChatIds(STANDARD_LIST_INPUT).collect(Collectors.toList());
        assertThat(chatIdExtracts, is(STANDARD_LIST));
    }

    @Test(expected = NumberFormatException.class)
    public void we_get_exceptions_while_extracting_chat_id_given_invalid_input() {
        ChatIdUtil.extractChatIds(RUBBISH_INPUT);
    }

    @Test
    public void correct_input_is_marked_as_valid() {
        assertTrue(ChatIdUtil.isChatIdInputValid(STANDARD_LIST_INPUT));
    }

    @Test
    public void rubbish_input_is_marked_as_invalid() {
        assertFalse(ChatIdUtil.isChatIdInputValid(RUBBISH_INPUT));
    }

    @Test
    public void we_can_get_chat_ids_from_users_with_valid_input() {
        Set<SUser> users = new HashSet<>(Arrays.asList(generateUserWithInput(STANDARD_LIST_INPUT)));
        assertThat(ChatIdUtil.collectChatIdsFromUsers(users), is(STANDARD_LIST));
    }

    @Test
    public void we_ignore_users_with_invalid_input() {
        Set<SUser> users = new HashSet<>(Arrays.asList(generateUserWithInput(RUBBISH_INPUT)));
        assertThat(ChatIdUtil.collectChatIdsFromUsers(users), is(empty()));
    }

    @Test
    public void chat_ids_are_not_repeated() {
        List<Long> second_list = new ArrayList<>(STANDARD_LIST);
        second_list.add(444L);
        String snd_input = second_list.stream().map(Object::toString).collect(Collectors.joining(";"));
        Set<SUser> users = new HashSet<>(
                Arrays.asList(
                        generateUserWithInput(STANDARD_LIST_INPUT),
                        generateUserWithInput(snd_input)
                )
        );
        List<Long> extractedIds = ChatIdUtil.collectChatIdsFromUsers(users);
        assertThat(extractedIds.size(), is(STANDARD_LIST.size() + 1));
        assertThat(extractedIds, is(second_list));
    }

    private SUser generateUserWithInput(String input) {
        SUser mockedUser = Mockito.mock(SUser.class);
        Mockito.when(mockedUser.getPropertyValue(TelegramNotificator.TELEGRAM_PROP_KEY)).thenReturn(input);
        return mockedUser;
    }
}
