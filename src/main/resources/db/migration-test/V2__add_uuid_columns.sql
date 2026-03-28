ALTER TABLE pauta ADD COLUMN uuid VARCHAR(36);
ALTER TABLE sessao_votacao ADD COLUMN uuid VARCHAR(36);
ALTER TABLE voto ADD COLUMN uuid VARCHAR(36);

UPDATE pauta SET uuid = RANDOM_UUID() WHERE uuid IS NULL;
UPDATE sessao_votacao SET uuid = RANDOM_UUID() WHERE uuid IS NULL;
UPDATE voto SET uuid = RANDOM_UUID() WHERE uuid IS NULL;

ALTER TABLE pauta ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE sessao_votacao ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE voto ALTER COLUMN uuid SET NOT NULL;

CREATE UNIQUE INDEX idx_pauta_uuid ON pauta(uuid);
CREATE UNIQUE INDEX idx_sessao_uuid ON sessao_votacao(uuid);
CREATE UNIQUE INDEX idx_voto_uuid ON voto(uuid);
