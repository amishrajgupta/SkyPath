# How to Upload SkyPath to GitHub

Your local repository is ready! Follow these steps to upload it to GitHub.

## Step 1: Create a GitHub Repository

1. Go to [github.com/new](https://github.com/new)
2. Log in with your GitHub account (create one if you don't have it)
3. Fill in the repository details:
   - **Repository name:** `skypath` (or your preferred name)
   - **Description:** "Production-ready flight search engine with timezone handling and intelligent connection validation"
   - **Visibility:** `Public` ✅
   - **Do NOT initialize with:**
     - README.md
     - .gitignore
     - License
   
   (You already have these locally)

4. Click **Create repository**

## Step 2: Connect Your Local Repository to GitHub

Copy the HTTPS URL from your newly created GitHub repository (it looks like: `https://github.com/YOUR_USERNAME/skypath.git`)

Then run these commands in your terminal:

```bash
cd C:\Users\Amishraj\OneDrive\Desktop\projects\SkyPath

# Add the remote (replace with YOUR actual repository URL)
git remote add origin https://github.com/YOUR_USERNAME/skypath.git

# Verify the remote was added
git remote -v
```

You should see:
```
origin  https://github.com/YOUR_USERNAME/skypath.git (fetch)
origin  https://github.com/YOUR_USERNAME/skypath.git (push)
```

## Step 3: Push to GitHub

```bash
# Set the default branch to main (GitHub uses 'main' by default now)
git branch -M main

# Push all commits and set the upstream
git push -u origin main
```

You'll be prompted to authenticate with GitHub:
- Enter your GitHub username
- Enter a Personal Access Token (PAT) as password
  
  If you don't have a PAT:
  1. Go to [github.com/settings/tokens](https://github.com/settings/tokens)
  2. Click "Generate new token"
  3. Select scopes: `repo` (full control of private repositories)
  4. Copy the token and paste it in the terminal

## Step 4: Verify Your Repository

1. Go to `https://github.com/YOUR_USERNAME/skypath`
2. You should see:
   - ✅ 16 Java source files
   - ✅ 2 commits in commit history
   - ✅ All documentation files (README.md, CONTRIBUTING.md, LICENSE)
   - ✅ flights.json dataset

## Step 5: Configure GitHub Repository Settings (Optional but Recommended)

1. Go to **Settings** → **General**
   - Enable "Discussions" for Q&A
   - Enable "Issues" for bug reports

2. Go to **Settings** → **Code security** → **Branch protection rules**
   - Protect the `main` branch
   - Require pull request reviews before merging (1+)

3. Go to the repository **About** section (top right)
   - Edit and add:
     - **Description:** "Production-ready flight search engine with timezone handling"
     - **Website:** (leave for now)
     - **Topics:** Add tags like:
       - `java`
       - `flight-search`
       - `concurrency`
       - `thread-safe`
       - `design-patterns`
       - `algorithm`

## Step 6: Share Your Repository

Once pushed, share the link:
```
https://github.com/YOUR_USERNAME/skypath
```

---

## Troubleshooting

### "fatal: the current branch master has no upstream branch"
Solution:
```bash
git branch -M main
git push -u origin main
```

### "Authentication failed"
1. Your GitHub password alone won't work
2. Use a Personal Access Token instead:
   - Go to [github.com/settings/tokens](https://github.com/settings/tokens)
   - Generate new token with `repo` scope
   - Use token as password in git

### "Repository already exists"
You may have created the repo. Check `git remote -v` and verify it's correct.

### "Connection refused"
Check your internet connection and try again.

---

## What Gets Uploaded

✅ **Source Code (16 files)**
```
src/
├── Main.java
├── entities/ (4 files)
├── enums/ (1 file)
├── repositories/ (2 files)
├── services/ (3 files)
└── utilities/ (2 files)
```

✅ **Documentation**
```
README.md
CONTRIBUTING.md
LICENSE
GITHUB_SETUP.md
```

✅ **Data**
```
flights.json (303 flights, 25 airports)
```

✅ **Configuration**
```
.gitignore
SkyPath.iml
```

❌ **NOT Uploaded** (in .gitignore)
```
out/                    # Compiled classes
.idea/                  # IDE cache
*.class                 # Bytecode
```

---

## Commit History

Your repository will show 2 commits:

1. **Initial commit** - All 16 Java files + configuration
   - 22 files changed
   - Complete source code

2. **docs: add GitHub contribution guidelines** - GitHub-specific files
   - Contributing guide
   - MIT License
   - Issue templates

This shows your development process cleanly!

---

## Next Steps

After uploading:

1. ✅ Test the clone:
   ```bash
   cd /tmp
   git clone https://github.com/YOUR_USERNAME/skypath.git
   cd skypath
   # Try running tests
   ```

2. ✅ Add topics and description in GitHub settings

3. ✅ Share with friends/colleagues for feedback

4. ✅ Consider adding:
   - CI/CD pipeline (GitHub Actions)
   - Badges for README
   - Project board for tracking ideas

---

## Sample Repository URL
```
https://github.com/YOUR_USERNAME/skypath
```

**Replace `YOUR_USERNAME` with your actual GitHub username!**

---

Questions? Check:
- [GitHub Help - Creating a repository](https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-new-repository)
- [GitHub Help - Adding a local repository to GitHub](https://docs.github.com/en/migrations/importing-source-code/using-the-command-line-to-import-source-code/adding-locally-hosted-code-to-github)

Good luck! 🚀

