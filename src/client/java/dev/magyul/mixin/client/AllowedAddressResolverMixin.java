package dev.magyul.mixin.client;

import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.RedirectResolver;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.Type;

import java.util.Optional;

@Mixin(AllowedAddressResolver.class)
public class AllowedAddressResolverMixin {

    @Redirect(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/RedirectResolver;lookupRedirect(Lnet/minecraft/client/network/ServerAddress;)Ljava/util/Optional;"))
    private Optional<ServerAddress> resolve$lookupRedirect(RedirectResolver redirectResolver, ServerAddress address) {
        Optional<ServerAddress> optional = redirectResolver.lookupRedirect(address);
        if (optional.isEmpty()) {
            optional = lookupRedirect(address);
        }
        return optional;
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
