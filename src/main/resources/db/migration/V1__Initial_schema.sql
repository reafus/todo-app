CREATE TABLE notes_group (
                             id BIGSERIAL PRIMARY KEY,
                             name TEXT NOT NULL,
                             created_date TIMESTAMP NOT NULL,
                             last_modified_date TIMESTAMP
);

CREATE TABLE note (
                      id BIGSERIAL PRIMARY KEY,
                      title TEXT NOT NULL,
                      content TEXT,
                      is_completed BOOLEAN DEFAULT FALSE,
                      created_date TIMESTAMP NOT NULL,
                      last_modified_date TIMESTAMP,
                      group_id BIGINT REFERENCES notes_group(id) ON DELETE CASCADE,
                      parent_note_id BIGINT REFERENCES note(id) ON DELETE CASCADE,
                      todo_date DATE
);

CREATE INDEX idx_note_group_id ON note (group_id);
CREATE INDEX idx_note_parent_note_id ON note (parent_note_id);
CREATE INDEX idx_note_todo_date ON note (todo_date);