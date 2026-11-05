ALTER TABLE professor ADD COLUMN email_verificado BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE professor ADD COLUMN verification_token VARCHAR(255);
ALTER TABLE professor ADD COLUMN token_expira_em TIMESTAMP WITH TIME ZONE;

-- Marca os professores existentes como verificados (para não travar ninguém)
UPDATE professor SET email_verificado = TRUE;