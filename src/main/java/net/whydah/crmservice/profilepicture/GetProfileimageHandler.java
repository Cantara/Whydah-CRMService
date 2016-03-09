package net.whydah.crmservice.profilepicture;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Singleton
public class GetProfileImageHandler implements Handler {

    private final ProfileImageRepository repository;

    @Inject
    public GetProfileImageHandler(ProfileImageRepository repository) {
        this.repository = repository;
    }
    @Override
    public void handle(Context ctx) throws Exception {

        String customerRef = ctx.getPathTokens().get("customerRef");

        Blocking.get(() -> repository.getProfileImage(customerRef)).then(profilePicture -> {
            if (profilePicture != null && profilePicture.getData() != null) {
                ctx.getResponse().getHeaders().add("Content-Disposition", "inline");
                ctx.getResponse().getHeaders().add("Content-Type", profilePicture.getContentType());
                ctx.getResponse().send(profilePicture.getData());
            } else {
                ctx.clientError(404); //Not found
            }
        });
    }
}
