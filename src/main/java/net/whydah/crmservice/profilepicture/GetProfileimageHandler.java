package net.whydah.crmservice.profilepicture;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Singleton
public class GetProfileimageHandler implements Handler {

    private final ProfilepictureRepository repository;

    @Inject
    public GetProfileimageHandler(ProfilepictureRepository repository) {
        this.repository = repository;
    }
    @Override
    public void handle(Context ctx) throws Exception {

        String customerRef = ctx.getPathTokens().get("customerRef");

        Blocking.get(() -> repository.getProfileimage(customerRef)).then(profilePicture -> {
            if (profilePicture != null && profilePicture.getImageData() != null) {
                ctx.getResponse().getHeaders().add("Content-Disposition", "inline");
                ctx.getResponse().getHeaders().add("Content-Type", profilePicture.getContentType());
                ctx.getResponse().send(profilePicture.getImageData());
            } else {
                ctx.clientError(404); //Not found
            }
        });
    }
}
