package com.chaosroll.achievement;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AchievementRegistry {

    private AchievementRegistry() {}

    public static final Map<String, Achievement> ALL = new LinkedHashMap<>();

    static {
        register(new Achievement("first_roll", "Перший крок", "Крутни рулетку вперше", 1));
        register(new Achievement("roller_10", "Любитель", "Прокрути 10 разів", 10));
        register(new Achievement("roller_100", "Завсідник", "Прокрути 100 разів", 100));
        register(new Achievement("roller_500", "Майстер хаосу", "Прокрути 500 разів", 500));
        register(new Achievement("positive_25", "Везунчик", "25 позитивних подій", 25));
        register(new Achievement("negative_25", "Мученик", "25 негативних подій", 25));
        register(new Achievement("chaotic_25", "Хаотик", "25 хаотичних подій", 25));
        register(new Achievement("positive_streak_5", "Удача підряд", "5 позитивних підряд", 5));
        register(new Achievement("negative_streak_5", "Прокляття долі", "5 негативних підряд", 5));
        register(new Achievement("tornado_survived", "Вижив у торнадо", "Пережити подію 'Торнадо'", 1));
        register(new Achievement("morph_5", "Перевтілення", "5 разів стати мобом", 5));
        register(new Achievement("screen_flipped", "Світ догори дном", "Пройти Screen Flip раз", 1));
        register(new Achievement("guardian_used", "Янгол врятував", "Янгол-охоронець спрацював", 1));
        register(new Achievement("vote_skipped_5", "Дипломат", "5 разів пропустити подію голосуванням", 5));
        register(new Achievement("survived_30min", "Витривалий", "30 хвилин у грі без смерті", 1));
    }

    private static void register(Achievement a) {
        ALL.put(a.id, a);
    }

    public static Achievement get(String id) {
        return ALL.get(id);
    }
}
