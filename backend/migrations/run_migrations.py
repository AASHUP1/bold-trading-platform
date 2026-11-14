import os
import sys
from pathlib import Path
import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT

def run():
    database_url = os.getenv("DATABASE_URL", "postgresql://postgres:postgres@localhost:5432/bold_platform")
    print(f"Connecting to database: {database_url.split('@')[-1] if '@' in database_url else 'local'}")
    
    migrations_dir = Path(__file__).parent
    sql_files = sorted(migrations_dir.glob("*.sql"))
    
    try:
        conn = psycopg2.connect(database_url)
        conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
        cursor = conn.cursor()
        
        for sql_file in sql_files:
            print(f"Applying migration: {sql_file.name}...")
            with open(sql_file, "r", encoding="utf-8") as f:
                sql_content = f.read()
            cursor.execute(sql_content)
            print(f"  [OK] Applied {sql_file.name}")
            
        print("\nAll 7 migrations applied successfully!")
        cursor.close()
        conn.close()
    except Exception as e:
        print(f"Migration error: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    run()
