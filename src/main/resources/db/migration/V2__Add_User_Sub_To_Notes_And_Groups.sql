ALTER TABLE notes_group ADD COLUMN user_sub VARCHAR(255);

CREATE INDEX idx_notes_group_user_sub ON notes_group (user_sub);

ALTER TABLE note ADD COLUMN user_sub VARCHAR(255);

CREATE INDEX idx_note_user_sub ON note (user_sub);
