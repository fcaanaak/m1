import {networkInterfaces} from 'os';
import {NetworkInterfaceInfo, NetworkInterfaceInfoIPv4, NetworkInterfaceInfoIPv6} from "node:os";

class IPService {

    private IPEntryIsValid(entry: NetworkInterfaceInfo): boolean {
        return !entry.internal && entry.family === "IPv4";
    }

    getIPAddress() {

        const interfaces = networkInterfaces();

        for (const netInterfaceEntries of Object.values(interfaces)) {

            for (const entry of netInterfaceEntries ?? []) {
                if (this.IPEntryIsValid(entry)) {
                    return entry.address;
                }
            }

        }

        return null;

    }

}

export default IPService;