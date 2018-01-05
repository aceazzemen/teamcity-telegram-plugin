package com.notononoto.teamcity.telegram;

import jetbrains.buildServer.users.SUser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ChatIdUtil {

    public static Stream<Long> extractChatIds(@NotNull String chatIdsInput) throws NumberFormatException {
        String[] chatIdStrings = chatIdsInput.split(";");
        List<Long> chatIds = new ArrayList<>();
        for (String id : chatIdStrings) {
            chatIds.add(Long.parseLong(id));
        }
        return chatIds.stream();
    }

    public static List<Long> collectChatIdsFromUsers(@NotNull Set<SUser> users) {
        return users.stream()
                .map(user -> user.getPropertyValue(TelegramNotificator.TELEGRAM_PROP_KEY))
                .filter(Objects::nonNull)
                // looks like new Teamcity don't validate input with validator in user properties
                // so we should check input before send (TW-47469). It's fixed at bugtrack but looks like
                // it's still reproducing...
                .filter(ChatIdUtil::isChatIdInputValid)
                .flatMap(ChatIdUtil::extractChatIds)
                .distinct()
                .collect(Collectors.toList());
    }

    public static boolean isChatIdInputValid(@NotNull String value) {
        try {
            extractChatIds(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
