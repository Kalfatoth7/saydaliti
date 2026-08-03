import os
import zipfile

def zipdir(path, ziph):
    for root, dirs, files in os.walk(path):
        # Exclude directories
        if any(excluded in root for excluded in ['.git', 'build', '.gradle', '.idea', 'node_modules']):
            continue
        for file in files:
            file_path = os.path.join(root, file)
            # Skip the zip file itself and the script
            if file_path.endswith('AlRahmaHealthOS.zip') or file_path.endswith('zip_project.py'):
                continue
            ziph.write(file_path, os.path.relpath(file_path, path))

with zipfile.ZipFile('AlRahmaHealthOS.zip', 'w', zipfile.ZIP_DEFLATED) as zipf:
    zipdir('.', zipf)
