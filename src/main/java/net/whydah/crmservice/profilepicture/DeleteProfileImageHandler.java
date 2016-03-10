package net.whydah.crmservice.profilepicture;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Singleton
public class DeleteProfileImageHandler implements Handler {

    private final ProfileImageRepository repository;

    @Inject
    public DeleteProfileImageHandler(ProfileImageRepository repository) {
        this.repository = repository;
    }
    @Override
    public void handle(Context ctx) throws Exception {

        String customerRef = ctx.getPathTokens().get("customerRef");

        Blocking.get(() -> repository.deleteProfileImage(customerRef)).then(affectedRows -> {
            if (affectedRows == 1) {
                ctx.redirect(204, customerRef); //No content
            } else {
                ctx.clientError(404); //Not found
            }
        });
    }
}
