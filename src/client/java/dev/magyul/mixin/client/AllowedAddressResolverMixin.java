package dev.magyul.mixin.client;

import net.minecraft.client.network.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.Type;

import java.util.Optional;

@Mixin(AllowedAddressResolver.class)
public class AllowedAddressResolverMixin {
    @Shadow @Final private AddressResolver addressResolver;

    @Shadow @Final private BlockListChecker blockListChecker;

    @Shadow @Final private RedirectResolver redirectResolver;

    @Inject(method = "resolve", at = @At("HEAD"), cancellable = true)
    private void resolve(ServerAddress address, CallbackInfoReturnable<Optional<Address>> cb) {
        Optional<Address> optional = this.addressResolver.resolve(address);
        if ((optional.isEmpty() || this.blockListChecker.isAllowed(optional.get())) && this.blockListChecker.isAllowed(address)) {
            Optional<ServerAddress> optional2 = this.redirectResolver.lookupRedirect(address);
            if (optional2.isEmpty()) {
                optional2 = lookupRedirect(address);
            }
            if (optional2.isPresent()) {
                Optional<Address> var10000 = this.addressResolver.resolve(optional2.get());
                optional = var10000.filter(this.blockListChecker::isAllowed);
            }

            cb.setReturnValue(optional);
        } else {
            cb.setReturnValue(Optional.empty());
        }
    }

    @Unique
    private Optional<ServerAddress> lookupRedirect(ServerAddress address) {
        if (address.getPort() == 25565) {
            try {
                var records = new Lookup("_minecraft._tcp." + address.getAddress(), Type.SRV).run();

                if (records != null && records.length > 0) {
                    SRVRecord srv = (SRVRecord) records[0];
                    var host = srv.getTarget().toString();
                    var port = srv.getPort();
                    return Optional.of(new ServerAddress(host, port));
                }
            } catch (Throwable ignored) {}
        }

        return Optional.empty();
    }
}
