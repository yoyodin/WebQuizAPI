package org.example.webquizapi.config;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:routes.properties")
public class RoutesConfig {
    public static final String API_URI          = "${api.root}/${api.version}";

    // AppUser routes
    public static final String APP_USER_ROOT    = API_URI + "${user.root}";
    public static final String REGISTER         = "${user.register}";
    public static final String TOKEN            = "${user.token}";

    // Quiz routes
    public static final String QUIZ_ROOT        = API_URI + "${quiz.root}";
    public static final String ALL_QUIZZES      = "${quiz.all}";
    public static final String QUIZ_BY_ID       = "${quiz.by.id}";
    public static final String SOLVE_BY_ID      = "${quiz.solve.by.id}";
    public static final String SOLVED_QUIZZES   = "${quiz.solved}";
}
