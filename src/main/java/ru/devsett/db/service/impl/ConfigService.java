package ru.devsett.db.service.impl;

import org.springframework.stereotype.Service;
import ru.devsett.db.dto.BankEntity;
import ru.devsett.db.dto.ConfigEntity;
import ru.devsett.db.repository.BankRepository;
import ru.devsett.db.repository.ConfigRepository;

@Service
public class ConfigService {

    private final ConfigRepository configRepository;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }


    public ConfigEntity getWebCamEntity() {
        var webcam = configRepository.findOneByName("EnableWebCam");

        if (webcam == null) {
            var ent = new ConfigEntity();
            ent.setName("EnableWebCam");
            ent.setEnabled(false);
            return configRepository.save(ent);
        }

        return webcam;
    }

    public ConfigEntity revertWebCam() {
        var conf = getWebCamEntity();

        conf.setEnabled(!conf.isEnabled());

       return configRepository.save(conf);
    }

    public void enableWebCam() {
        var conf = getWebCamEntity();

        conf.setEnabled(true);

        configRepository.save(conf);
    }

    public void disableWebCam() {
        var conf = getWebCamEntity();

        conf.setEnabled(false);

        configRepository.save(conf);
    }

    public boolean isEnableWebCam() {
        return getWebCamEntity().isEnabled();
    }
}
