ALTER TABLE agenda ADD COLUMN uuid VARCHAR(36);
ALTER TABLE voting_session ADD COLUMN uuid VARCHAR(36);
ALTER TABLE vote ADD COLUMN uuid VARCHAR(36);

UPDATE agenda SET uuid = RANDOM_UUID() WHERE uuid IS NULL;
UPDATE voting_session SET uuid = RANDOM_UUID() WHERE uuid IS NULL;
UPDATE vote SET uuid = RANDOM_UUID() WHERE uuid IS NULL;

ALTER TABLE agenda ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE voting_session ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE vote ALTER COLUMN uuid SET NOT NULL;

CREATE UNIQUE INDEX idx_agenda_uuid ON agenda(uuid);
CREATE UNIQUE INDEX idx_voting_session_uuid ON voting_session(uuid);
CREATE UNIQUE INDEX idx_vote_uuid ON vote(uuid);
