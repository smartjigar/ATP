package org.catenax.atp.web;

import org.catenax.atp.model.Asset;
import org.catenax.atp.utils.constant.AppConstants;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(AppConstants.APP_CONTEXT_PATH + "/asset")
public class AssetResource {

    public Map<String, Asset> assetMap = new HashMap<>();


    @GetMapping()
    public Map<String, Asset> getAssets() {
        return this.assetMap;
    }

    @PostMapping()
    public void createAsset(@RequestBody Asset asset) {
        this.assetMap.put(asset.id(), asset);
    }

    @PutMapping()
    public void UpdateAsset(@RequestBody Asset asset) {
        this.assetMap.put(asset.id(), asset);
    }

    @DeleteMapping("/{id}")
    public void UpdateAsset(@PathVariable("id") String id) {
        this.assetMap.remove(id);
    }

    @GetMapping("/{id}")
    public Asset getAssetById(@PathVariable("id") String id) {
        return this.assetMap.get(id);
    }


}
