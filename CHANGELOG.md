# Changelog


## [1.4.0](https://github.com/nyg/jmxsh/compare/v1.3.1..v1.4.0) - 2026-06-07

### ⛰️  Features

- [`3b2b3a1`](https://github.com/nyg/jmxsh/commit/3b2b3a18ebde66f3b40b9d8275f162be5d89541c) Configurable prompt, clean UX — remove # prefix, Ctrl+C, domain/bean sync ([#67](https://github.com/nyg/jmxsh/issues/67))
- [`cbb97f2`](https://github.com/nyg/jmxsh/commit/cbb97f227272174dfa0bc0788207555c2ba88bf7) *(cmd)* Deduplicate JLine command history in-session and on disk ([#66](https://github.com/nyg/jmxsh/issues/66))

### 🐛 Bug Fixes

- [`6989d2a`](https://github.com/nyg/jmxsh/commit/6989d2a33b3a1dc35e11a04eaaf9a929229bdedf) *(deps)* Update all stable non-major dependencies ([#63](https://github.com/nyg/jmxsh/issues/63))
- [`b59fedb`](https://github.com/nyg/jmxsh/commit/b59fedbe15c5e9b14a6e50086be04a53f6a68048) *(cmd)* Suppress welcome message when using -n flag ([#62](https://github.com/nyg/jmxsh/issues/62))

### ⚙️ Miscellaneous

- [`678b76f`](https://github.com/nyg/jmxsh/commit/678b76fc3d79de4c81b62ab457f6e94bc69b8c82) *(deps)* Pin dependencies ([#64](https://github.com/nyg/jmxsh/issues/64))
- [`67d10b8`](https://github.com/nyg/jmxsh/commit/67d10b82863450f79ac5d1bf18b395b98accfe61) *(deps)* Update actions/checkout digest to df4cb1c ([#65](https://github.com/nyg/jmxsh/issues/65))

## [1.3.1](https://github.com/nyg/jmxsh/compare/v1.3.0..v1.3.1) - 2026-05-30

### ⛰️  Features

- [`29ef859`](https://github.com/nyg/jmxsh/commit/29ef859ef6887a3bb64535a08abbb34eb29f2750) *(io)* **[breaking]** Add logback logging, XDG config, quiet mode ([#44](https://github.com/nyg/jmxsh/issues/44))

### 🐛 Bug Fixes

- [`7d5cfb0`](https://github.com/nyg/jmxsh/commit/7d5cfb087aaa72475311bb6947da600dc0ef6847) *(deps)* Update all stable non-major dependencies ([#58](https://github.com/nyg/jmxsh/issues/58))
- [`0a9444b`](https://github.com/nyg/jmxsh/commit/0a9444bc7fc085f5afdc59a33627eb342e2051b1) Remediate SonarQube issues ([#55](https://github.com/nyg/jmxsh/issues/55))

### 🚜 Refactor

- [`d597de4`](https://github.com/nyg/jmxsh/commit/d597de467ae49662c28fbcebc0bc638610fc6510) Convert CommandInput/Output to interfaces, simplify WatchCommand ([#53](https://github.com/nyg/jmxsh/issues/53))
- [`bf4b6eb`](https://github.com/nyg/jmxsh/commit/bf4b6eba6b7b72e5844eb702b3f73eb5e8b9ca34) Replace EscapingTokenizer with JLine DefaultParser ([#52](https://github.com/nyg/jmxsh/issues/52))
- [`f0ca73b`](https://github.com/nyg/jmxsh/commit/f0ca73bc872fc66abb5b0dd7710b8508b4e618c0) Replace reflection-based command instantiation with suppliers ([#51](https://github.com/nyg/jmxsh/issues/51))
- [`706aaae`](https://github.com/nyg/jmxsh/commit/706aaae20a39d75107b5865fa27d0fa9961e7eba) Collapse abstractions in Session, Connection, and process management ([#50](https://github.com/nyg/jmxsh/issues/50))
- [`a0d0b1a`](https://github.com/nyg/jmxsh/commit/a0d0b1ae6233f7513485a680cfbf578f87ac37c4) **[breaking]** Modernize legacy code patterns ([#49](https://github.com/nyg/jmxsh/issues/49))
- [`2339b7f`](https://github.com/nyg/jmxsh/commit/2339b7f1ccb3e38e99f43fce2b07a04e10764965) Rename package to sh.jmx.jmxsh ([#48](https://github.com/nyg/jmxsh/issues/48))

### 📚 Documentation

- [`6935ca0`](https://github.com/nyg/jmxsh/commit/6935ca0af17077c547917e5bd7c0da2d8be72b40) Use DEB822 format for apt install instructions ([#60](https://github.com/nyg/jmxsh/issues/60))

### ⚙️ Miscellaneous

- [`1f5597c`](https://github.com/nyg/jmxsh/commit/1f5597cd98956b0c35a691b5da84b9d5fbe6f1b4) Dispatch formula update to homebrew-tap ([#61](https://github.com/nyg/jmxsh/issues/61))
- [`1a2f82e`](https://github.com/nyg/jmxsh/commit/1a2f82eb5bfd97f0cc405e183704a3c4340233b6) *(deps)* Update orhun/git-cliff-action digest to f50e115 ([#57](https://github.com/nyg/jmxsh/issues/57))
- [`49c78df`](https://github.com/nyg/jmxsh/commit/49c78df4e072c42777da39183991ce4fc9c8d913) Add JaCoCo coverage and SonarCloud analysis ([#54](https://github.com/nyg/jmxsh/issues/54))
- [`e9d0847`](https://github.com/nyg/jmxsh/commit/e9d0847e06eba63a673955e94d9e2e271b12c967) Remove @author javadoc tags ([#47](https://github.com/nyg/jmxsh/issues/47))
- [`fb4c826`](https://github.com/nyg/jmxsh/commit/fb4c826b2dd8e92856ac12f93721f81eb6083b55) Add Lombok and use it throughout the codebase ([#46](https://github.com/nyg/jmxsh/issues/46))

## [1.3.0](https://github.com/nyg/jmxsh/compare/v1.2.3..v1.3.0) - 2026-04-17

### ⛰️  Features

- [`60335ca`](https://github.com/nyg/jmxsh/commit/60335ca3731589c90c1d1a83500e25a0ccf51915) XDG Base Directory compliance for command history ([#41](https://github.com/nyg/jmxsh/issues/41))
- [`3a646c4`](https://github.com/nyg/jmxsh/commit/3a646c4ff78d2d605b8d5795ba6ca44f3594f013) *(deps)* Migrate from JCLI to Picocli for argument parsing ([#38](https://github.com/nyg/jmxsh/issues/38))

### ⚙️ Miscellaneous

- [`58f9333`](https://github.com/nyg/jmxsh/commit/58f93332f2b5eb1d61219e5e76d61e9d1ddfae87) Add i386 architecture support to apt repository ([#43](https://github.com/nyg/jmxsh/issues/43))

## [1.2.3](https://github.com/nyg/jmxsh/compare/v1.2.2..v1.2.3) - 2026-04-11

### 🐛 Bug Fixes

- [`b216aeb`](https://github.com/nyg/jmxsh/commit/b216aeb45069c5edd21aaabf588ca87f980577cd) *(build)* Correct patch release version calculation and update README ([#35](https://github.com/nyg/jmxsh/issues/35))

### 🚜 Refactor

- [`7e83d7f`](https://github.com/nyg/jmxsh/commit/7e83d7f36a69a29cb1a9b41a1b6fd967d3caf32f) Java language modernization ([#37](https://github.com/nyg/jmxsh/issues/37))

### 📚 Documentation

- [`8c29928`](https://github.com/nyg/jmxsh/commit/8c29928f4232d8ba40e34cb4f17031a7b6d809d3) Update TODO.md ([#36](https://github.com/nyg/jmxsh/issues/36))

## [1.2.2](https://github.com/nyg/jmxsh/compare/v1.2.0..v1.2.2) - 2026-04-11

### 🐛 Bug Fixes

- [`b252556`](https://github.com/nyg/jmxsh/commit/b2525567bc5097e282994bf0668d0bf2f24b6082) *(deps)* Update dependency org.jline:jline to v4.0.12 ([#34](https://github.com/nyg/jmxsh/issues/34))

### 📚 Documentation

- [`70d5cbe`](https://github.com/nyg/jmxsh/commit/70d5cbeb28677e6cb5c9b99d1bbeb5c5fd68cf01) Add Homebrew installation instructions ([#30](https://github.com/nyg/jmxsh/issues/30))

### ⚙️ Miscellaneous

- [`b8bbc5e`](https://github.com/nyg/jmxsh/commit/b8bbc5ea97f803205f1e41b9e18c7579dd412036) *(deps)* Update peter-evans/repository-dispatch action to v4 ([#33](https://github.com/nyg/jmxsh/issues/33))
- [`8e33650`](https://github.com/nyg/jmxsh/commit/8e33650c9b87eae6997cbd795b73099a911bdf3f) Add self-hosted apt repository at jmx.sh/apt ([#32](https://github.com/nyg/jmxsh/issues/32))
- [`255274d`](https://github.com/nyg/jmxsh/commit/255274d2229703f86f39f1e626e4f561ca89c4fe) Dispatch to Homebrew tap after release ([#31](https://github.com/nyg/jmxsh/issues/31))

## [1.2.0](https://github.com/nyg/jmxsh/compare/v1.1.0..v1.2.0) - 2026-04-11

### ⛰️  Features

- [`7553af1`](https://github.com/nyg/jmxsh/commit/7553af16fc8149f6489f2aca3a9074f7bea0c54a) *(ci)* Improve release version logic and JAR naming ([#27](https://github.com/nyg/jmxsh/issues/27))
- [`00dfbec`](https://github.com/nyg/jmxsh/commit/00dfbec28ec05320457f8d0e2d27261ff47c3171) *(ci)* Simplify release workflow to single bump-type input ([#24](https://github.com/nyg/jmxsh/issues/24))
- [`44af5a2`](https://github.com/nyg/jmxsh/commit/44af5a2752d7157f3189d95a03cf7e20ac29e16f) *(ci)* Add release automation workflow ([#18](https://github.com/nyg/jmxsh/issues/18))
- [`0ac80cd`](https://github.com/nyg/jmxsh/commit/0ac80cd7fa0fb42d04568ab69c74f22ea556fced) *(cmd)* Add JMXMP protocol support ([#16](https://github.com/nyg/jmxsh/issues/16))
- [`e17ca4e`](https://github.com/nyg/jmxsh/commit/e17ca4e82bfe118f5e3308d67abb358dd04eaec9) *(build)* Migrate to Java 25 only ([#9](https://github.com/nyg/jmxsh/issues/9))

### 🐛 Bug Fixes

- [`d029f5c`](https://github.com/nyg/jmxsh/commit/d029f5c864089defd6a3094c5b7e184370a18876) *(ci)* Use PAT to bypass ruleset during release push ([#26](https://github.com/nyg/jmxsh/issues/26))
- [`9718bf4`](https://github.com/nyg/jmxsh/commit/9718bf4bc866020926678ba75917660f705a6386) *(ci)* Install git-cliff binary before mvn release:prepare ([#25](https://github.com/nyg/jmxsh/issues/25))
- [`57f389f`](https://github.com/nyg/jmxsh/commit/57f389f0c41946745f87b22d67cf3d218fdd7625) *(deps)* Update all stable non-major dependencies ([#23](https://github.com/nyg/jmxsh/issues/23))
- [`d609283`](https://github.com/nyg/jmxsh/commit/d609283845d405a22e447259b7fd84fac2559f27) Restrict SyntaxUtils.isDigits to ASCII digits only ([#12](https://github.com/nyg/jmxsh/issues/12))

### 📚 Documentation

- [`76d90d3`](https://github.com/nyg/jmxsh/commit/76d90d3f915e9a1cf355bc7b6df7bb094a6e7bc4) Update stale references and add jmx.sh landing page ([#15](https://github.com/nyg/jmxsh/issues/15))
- [`ffa4406`](https://github.com/nyg/jmxsh/commit/ffa44069280009211c52e96143033b6e0dc86746) Add architecture documentation with Mermaid sequence diagram ([#6](https://github.com/nyg/jmxsh/issues/6))
- [`57d9682`](https://github.com/nyg/jmxsh/commit/57d96829beca05057391307437b5ec68c61e7734) Add build process documentation ([#7](https://github.com/nyg/jmxsh/issues/7))
- [`04b2cb1`](https://github.com/nyg/jmxsh/commit/04b2cb1d53eabfd0089ec62a224c0016d48cb84a) Rework README for better user experience ([#8](https://github.com/nyg/jmxsh/issues/8))

### 🧪 Testing

- [`4ec936a`](https://github.com/nyg/jmxsh/commit/4ec936adf156180ad7b8ed6c6d12f9088c9f6df9) Migrate from JMock to Mockito and JUnit asserts to AssertJ ([#13](https://github.com/nyg/jmxsh/issues/13))
- [`cc3bc98`](https://github.com/nyg/jmxsh/commit/cc3bc9888d27c10509617de43aebc232b222b292) Add two-tier test suite (integration tests + E2E tests) ([#5](https://github.com/nyg/jmxsh/issues/5))

### ⚙️ Miscellaneous

- [`bf3efa8`](https://github.com/nyg/jmxsh/commit/bf3efa8b0c75f0e798b33bde9bef6552d00a4eef) Add CHANGELOG.md, update script ([#28](https://github.com/nyg/jmxsh/issues/28))
- [`48dd1a2`](https://github.com/nyg/jmxsh/commit/48dd1a23bc6b95e414b1f6938fb3faf70dfba12d) *(ci)* Remove archived kenji-miyake/setup-git-cliff action ([#22](https://github.com/nyg/jmxsh/issues/22))
- [`2a4025c`](https://github.com/nyg/jmxsh/commit/2a4025cc3cbf24ec1e988d47ae9be422e197be28) *(deps)* Update orhun/git-cliff-action digest to c93ef52 ([#21](https://github.com/nyg/jmxsh/issues/21))
- [`abb51c4`](https://github.com/nyg/jmxsh/commit/abb51c4f40b5e96d4d5ac14e81aeaacd1ca7fe12) *(build)* Move DEB/RPM packaging to CI, remove Docker ([#17](https://github.com/nyg/jmxsh/issues/17))
- [`6500138`](https://github.com/nyg/jmxsh/commit/6500138256fd6891402b395a213c9ee21fc0e582) *(deps)* Pin amannn/action-semantic-pull-request action to 48f2562 ([#14](https://github.com/nyg/jmxsh/issues/14))
- [`588ce79`](https://github.com/nyg/jmxsh/commit/588ce79636423b63dab4f6ead3d82e726c4e4151) Switch uber JAR from assembly to shade plugin ([#11](https://github.com/nyg/jmxsh/issues/11))
- [`9e0861d`](https://github.com/nyg/jmxsh/commit/9e0861d9ac33158c9ecdd8ab4b56dc5af0326a71) Rename jmxterm to jmxsh ([#10](https://github.com/nyg/jmxsh/issues/10))
- [`a7fda26`](https://github.com/nyg/jmxsh/commit/a7fda26cd162757c730b6ae3fb43b68205cfefe3) *(deps)* Update docker/build-push-action action to v7 ([#3](https://github.com/nyg/jmxsh/issues/3))
- [`3d068ce`](https://github.com/nyg/jmxsh/commit/3d068ce3e1df8d310d5e4867613ebb0b209582e1) *(deps)* Pin dependencies ([#2](https://github.com/nyg/jmxsh/issues/2))
- [`96de2c5`](https://github.com/nyg/jmxsh/commit/96de2c5343ef5fe40eb299917fbd0b53c3317dbe) Update Copilot instructions, add TODO.md, disable nightly build, replace Dependabot with Renovate ([#1](https://github.com/nyg/jmxsh/issues/1))
- [`d5ad0f7`](https://github.com/nyg/jmxsh/commit/d5ad0f711c97b88bf4d4ee5a3f4be7e32cd112b3) *(deps)* Bump docker/login-action from 3.7.0 to 4.0.0 ([#216](https://github.com/nyg/jmxsh/issues/216))
- [`10d75c5`](https://github.com/nyg/jmxsh/commit/10d75c53879aa1c0bfefc4509c409119930636b3) *(deps)* Bump docker/metadata-action from 5.10.0 to 6.0.0 ([#215](https://github.com/nyg/jmxsh/issues/215))
- [`381781b`](https://github.com/nyg/jmxsh/commit/381781b53b56345f12e022f466945aa8642f61d7) *(deps)* Bump ytanikin/pr-conventional-commits from 1.5.1 to 1.5.2 ([#220](https://github.com/nyg/jmxsh/issues/220))
- [`a97f0f2`](https://github.com/nyg/jmxsh/commit/a97f0f24d619cf71f7fd3f3ad1a06c5ee05300d7) *(deps)* Bump org.jline:jline from 4.0.4 to 4.0.9 ([#219](https://github.com/nyg/jmxsh/issues/219))
- [`c00ada3`](https://github.com/nyg/jmxsh/commit/c00ada35762e5623aaa50595ae0efd497b533e70) *(deps)* Bump org.jline:jline from 4.0.0 to 4.0.4 ([#218](https://github.com/nyg/jmxsh/issues/218))
- [`185c77f`](https://github.com/nyg/jmxsh/commit/185c77f675f676b43894acd60df03a071e95f8a2) *(deps)* Bump docker/setup-buildx-action from 3.12.0 to 4.0.0 ([#213](https://github.com/nyg/jmxsh/issues/213))
- [`42cc874`](https://github.com/nyg/jmxsh/commit/42cc8748d61d0f83e9fee315bcac1ff1a7a115aa) *(deps)* Bump org.jline:jline from 3.30.6 to 4.0.0 ([#214](https://github.com/nyg/jmxsh/issues/214))
- [`ed7a63d`](https://github.com/nyg/jmxsh/commit/ed7a63dca665b818fda22a799b8091171ba9b541) *(deps)* Bump docker/setup-qemu-action from 3.7.0 to 4.0.0 ([#212](https://github.com/nyg/jmxsh/issues/212))
- [`48b9dcb`](https://github.com/nyg/jmxsh/commit/48b9dcb06ec98e0ce24393a9f9fb8df1722a0585) *(deps)* Bump actions/download-artifact from 7 to 8 ([#211](https://github.com/nyg/jmxsh/issues/211))
- [`d9164e7`](https://github.com/nyg/jmxsh/commit/d9164e7b706a937f99a8167618ebd6c855a28454) *(deps)* Bump actions/upload-artifact from 6 to 7 ([#210](https://github.com/nyg/jmxsh/issues/210))
- [`5cf7480`](https://github.com/nyg/jmxsh/commit/5cf74807d2bb0b3654f33dc80a2104405470be64) *(deps)* Bump net.bytebuddy:byte-buddy from 1.18.5 to 1.18.7 ([#209](https://github.com/nyg/jmxsh/issues/209))
- [`7983cbf`](https://github.com/nyg/jmxsh/commit/7983cbf77622bc98a265fe0e7d84366ec3e63bc1) *(deps)* Bump org.apache.maven.plugins:maven-surefire-plugin from 3.5.4 to 3.5.5 ([#208](https://github.com/nyg/jmxsh/issues/208))
- [`3aa4089`](https://github.com/nyg/jmxsh/commit/3aa4089c1af95ffab5fac5eca191f0aa514081f4) *(deps)* Bump net.bytebuddy:byte-buddy from 1.18.4 to 1.18.5 ([#207](https://github.com/nyg/jmxsh/issues/207))
- [`add7d0f`](https://github.com/nyg/jmxsh/commit/add7d0f13670aad08c21a7f40c02e47d8a46a4ea) *(deps)* Bump org.junit.jupiter:junit-jupiter from 6.0.2 to 6.0.3 ([#206](https://github.com/nyg/jmxsh/issues/206))
- [`4fc390f`](https://github.com/nyg/jmxsh/commit/4fc390f523aa49d74f065cea0752454364a4b7a3) *(deps)* Bump docker/build-push-action from 6.18.0 to 6.19.2 ([#205](https://github.com/nyg/jmxsh/issues/205))
- [`2452912`](https://github.com/nyg/jmxsh/commit/24529125f27389aa11e7545a3cfcb2b34efbed82) *(deps)* Bump org.apache.maven.plugins:maven-compiler-plugin from 3.14.1 to 3.15.0 ([#204](https://github.com/nyg/jmxsh/issues/204))
- [`33b3be7`](https://github.com/nyg/jmxsh/commit/33b3be78770353295a26c801956cb3aa80d84475) *(deps)* Bump docker/login-action from 3.6.0 to 3.7.0 ([#203](https://github.com/nyg/jmxsh/issues/203))
- [`da54b19`](https://github.com/nyg/jmxsh/commit/da54b19d9fe6fb752fd95e50799ef5edce8c83fa) *(deps)* Bump net.bytebuddy:byte-buddy from 1.18.3 to 1.18.4 ([#202](https://github.com/nyg/jmxsh/issues/202))
- [`155cd59`](https://github.com/nyg/jmxsh/commit/155cd594b654bd0b89cc4a27238ee9f0abe291c6) *(deps)* Bump org.junit.jupiter:junit-jupiter from 6.0.1 to 6.0.2 ([#201](https://github.com/nyg/jmxsh/issues/201))
- [`8a2d653`](https://github.com/nyg/jmxsh/commit/8a2d6539c956264aa6efac1c8688c4d8b3d663ea) *(deps)* Bump actions/download-artifact from 6 to 7
- [`bf496cc`](https://github.com/nyg/jmxsh/commit/bf496cce41504610ac4eb666a623b27dcce4c66e) *(deps)* Bump net.bytebuddy:byte-buddy from 1.18.2 to 1.18.3 ([#200](https://github.com/nyg/jmxsh/issues/200))
- [`620b8d0`](https://github.com/nyg/jmxsh/commit/620b8d0b93d242e6e40ea67d95e15d59f82739a5) *(deps)* Bump docker/setup-buildx-action from 3.11.1 to 3.12.0 ([#199](https://github.com/nyg/jmxsh/issues/199))
- [`3eb5bb1`](https://github.com/nyg/jmxsh/commit/3eb5bb1f2c2b956700fed503e69b348e21ed1558) *(deps)* Bump actions/upload-artifact from 5 to 6 ([#197](https://github.com/nyg/jmxsh/issues/197))
- [`9f271c8`](https://github.com/nyg/jmxsh/commit/9f271c89c3c021c71cba2244418cdc9cf8d22ddd) *(deps)* Bump org.apache.maven.plugins:maven-assembly-plugin from 3.7.1 to 3.8.0 ([#193](https://github.com/nyg/jmxsh/issues/193))
- [`88a05db`](https://github.com/nyg/jmxsh/commit/88a05db3634f30c8bc736067f468e4e78fd85657) *(deps)* Bump org.apache.commons:commons-configuration2 from 2.12.0 to 2.13.0 ([#192](https://github.com/nyg/jmxsh/issues/192))
- [`1f7a365`](https://github.com/nyg/jmxsh/commit/1f7a365d34ce69fd617756d4558caab398d17e33) *(deps)* Bump org.apache.commons:commons-text from 1.14.0 to 1.15.0 ([#196](https://github.com/nyg/jmxsh/issues/196))
- [`f716ef8`](https://github.com/nyg/jmxsh/commit/f716ef8c255781f5d70b32153a71043bce3d5961) *(deps)* Bump org.cyclopsgroup:jcli from 1.0.1 to 1.0.2 ([#195](https://github.com/nyg/jmxsh/issues/195))
- [`aad87de`](https://github.com/nyg/jmxsh/commit/aad87de54828b76dcc44db3274a3bf7170adbf18) *(deps)* Bump ytanikin/pr-conventional-commits from 1.4.2 to 1.5.1 ([#194](https://github.com/nyg/jmxsh/issues/194))
- [`0485e78`](https://github.com/nyg/jmxsh/commit/0485e7804b77ff0f6a4240d90f40804eaaa9a8d1) *(deps)* Bump net.bytebuddy:byte-buddy from 1.18.1 to 1.18.2 ([#191](https://github.com/nyg/jmxsh/issues/191))
- [`328785a`](https://github.com/nyg/jmxsh/commit/328785a2da3f89d60734964a7c82ba368459c19c) *(deps)* Bump org.apache.maven.plugins:maven-source-plugin from 3.3.1 to 3.4.0 ([#190](https://github.com/nyg/jmxsh/issues/190))
- [`15f1aa7`](https://github.com/nyg/jmxsh/commit/15f1aa7eca6e22bbbfa7aa1b1d0e8e77110e0fd3) *(deps)* Bump docker/metadata-action from 5.9.0 to 5.10.0 ([#189](https://github.com/nyg/jmxsh/issues/189))
- [`d79df76`](https://github.com/nyg/jmxsh/commit/d79df762b48a102361c6fd8219ebacc225e0c186) *(deps)* Bump docker/metadata-action from 5.8.0 to 5.9.0 ([#182](https://github.com/nyg/jmxsh/issues/182))
- [`469a22b`](https://github.com/nyg/jmxsh/commit/469a22bf29177f8fd5ba3893a31c14a91112fc21) *(deps)* Bump actions/checkout from 5 to 6 ([#188](https://github.com/nyg/jmxsh/issues/188))
- [`49d7936`](https://github.com/nyg/jmxsh/commit/49d79360d7d4117b6bb4f8c7d76dcfb57abbe6a8) *(deps)* Bump org.apache.maven.plugins:maven-jar-plugin from 3.4.2 to 3.5.0 ([#187](https://github.com/nyg/jmxsh/issues/187))
- [`8ec6dc5`](https://github.com/nyg/jmxsh/commit/8ec6dc545834c597d975baacb87a93ae31f327bd) *(deps)* Bump net.bytebuddy:byte-buddy from 1.17.8 to 1.18.1 ([#186](https://github.com/nyg/jmxsh/issues/186))
- [`3b612ae`](https://github.com/nyg/jmxsh/commit/3b612ae8e3e853c0edacee79b5a02dd0f659a417) *(deps)* Bump org.apache.commons:commons-lang3 from 3.19.0 to 3.20.0 ([#185](https://github.com/nyg/jmxsh/issues/185))
- [`b75334f`](https://github.com/nyg/jmxsh/commit/b75334fafbd6eea5348a0e92f851c468c1d0f25e) *(deps)* Bump commons-io:commons-io from 2.20.0 to 2.21.0 ([#184](https://github.com/nyg/jmxsh/issues/184))
- [`897d69e`](https://github.com/nyg/jmxsh/commit/897d69e1d3e53abd2110cd19638264daf7c8a2c9) *(deps)* Bump docker/setup-qemu-action from 3.6.0 to 3.7.0 ([#183](https://github.com/nyg/jmxsh/issues/183))
- [`78627d0`](https://github.com/nyg/jmxsh/commit/78627d074060ec43e4e2be1dd03468892a0fcb3d) *(deps)* Bump org.junit.jupiter:junit-jupiter from 6.0.0 to 6.0.1 ([#181](https://github.com/nyg/jmxsh/issues/181))
- [`4688a8d`](https://github.com/nyg/jmxsh/commit/4688a8d5a25af9c711cd0e8f9b5ee25343c7523d) *(deps)* Bump actions/upload-artifact from 4 to 5 ([#180](https://github.com/nyg/jmxsh/issues/180))
- [`a0d8af1`](https://github.com/nyg/jmxsh/commit/a0d8af189019e418caa84d048ed80b62a91ce20c) *(deps)* Bump actions/download-artifact from 5 to 6 ([#179](https://github.com/nyg/jmxsh/issues/179))
- [`b4577d4`](https://github.com/nyg/jmxsh/commit/b4577d410bf3ccdd9316ff1eefa5baf0a2cefa18) *(deps)* Bump actions/download-artifact from 4 to 5 ([#178](https://github.com/nyg/jmxsh/issues/178))

### Others

- [`b4151bf`](https://github.com/nyg/jmxsh/commit/b4151bf882bd946c0087fdca4a08457d723e5e8b) Add Python files to .gitignore

## [1.1.0-lemyst](https://github.com/LeMyst/jmxterm/compare/v1.0.11..v1.1.0-lemyst) - 2025-10-19

### ⛰️  Features

- [`da6085a`](https://github.com/LeMyst/jmxterm/commit/da6085a6b98fc9794a4fe45c28179ff5856dd745) *(ci)* Update create-release.yaml for multi-Java builds ([#173](https://github.com/LeMyst/jmxterm/issues/173))
- [`cd0c1c7`](https://github.com/LeMyst/jmxterm/commit/cd0c1c769551c0bd2162c93ba4335410c64aaf87) Add comprehensive Copilot instructions for JMXTerm development ([#157](https://github.com/LeMyst/jmxterm/issues/157))

### 🐛 Bug Fixes

- [`c8d7c4f`](https://github.com/LeMyst/jmxterm/commit/c8d7c4fee980eb6d199f81dc04300981d5b09284) Correct finalName property usage in Maven build configuration ([#177](https://github.com/LeMyst/jmxterm/issues/177))
- [`a53702b`](https://github.com/LeMyst/jmxterm/commit/a53702b8f2ca3c78c6c275419afa8d519405dd70) Update conventional PR workflow configuration ([#170](https://github.com/LeMyst/jmxterm/issues/170))

### ⚙️ Miscellaneous

- [`6a2ca55`](https://github.com/LeMyst/jmxterm/commit/6a2ca559c50cedf1f428bbeb9c619810ef55326e) Bump version to 1.1.0 in pom.xml ([#175](https://github.com/LeMyst/jmxterm/issues/175))
- [`4dcdcf1`](https://github.com/LeMyst/jmxterm/commit/4dcdcf19ed94b115d26b79cb4ba01f1c43ee3476) *(ci)* Update Maven build and artifact upload in workflow ([#176](https://github.com/LeMyst/jmxterm/issues/176))
- [`ca25d94`](https://github.com/LeMyst/jmxterm/commit/ca25d94265dbabbc88f01ece8116fa0241431c70) *(deps)* Migrate to JUnit 6 ([#172](https://github.com/LeMyst/jmxterm/issues/172))
- [`8070a9f`](https://github.com/LeMyst/jmxterm/commit/8070a9f9a8d76b09f023868ac1980e8beb28b904) *(ci)* Add JDK 25 to CI workflow matrix ([#171](https://github.com/LeMyst/jmxterm/issues/171))
- [`71f91e9`](https://github.com/LeMyst/jmxterm/commit/71f91e9f835c7e69c9e9561490e86ed6f355931b) Clean up code ([#166](https://github.com/LeMyst/jmxterm/issues/166))
- [`674f5c4`](https://github.com/LeMyst/jmxterm/commit/674f5c49573a371de683d30f326dd1fdb2d8cf09) *(deps)* Bump net.bytebuddy:byte-buddy from 1.17.7 to 1.17.8 ([#169](https://github.com/LeMyst/jmxterm/issues/169))
- [`8bf4dbd`](https://github.com/LeMyst/jmxterm/commit/8bf4dbd05f3a74ff3b4cf74bdc1e4142388b7218) *(deps)* Bump github/codeql-action from 3 to 4 ([#168](https://github.com/LeMyst/jmxterm/issues/168))
- [`87daea9`](https://github.com/LeMyst/jmxterm/commit/87daea9cd35c7d7230a3c93cda5ff74896848917) *(deps)* Bump com.google.guava:guava from 33.4.8-jre to 33.5.0-jre ([#165](https://github.com/LeMyst/jmxterm/issues/165))
- [`b623758`](https://github.com/LeMyst/jmxterm/commit/b623758356e6ddcb18ff2ede1f111f9862d37882) Add PR conventional commit validation workflow ([#164](https://github.com/LeMyst/jmxterm/issues/164))
- [`4d09bd1`](https://github.com/LeMyst/jmxterm/commit/4d09bd140d35c9d0b359a87faf3290f38d0a255f) *(deps)* Bump eclipse-temurin from 24-jre-alpine to 25-jre-alpine ([#162](https://github.com/LeMyst/jmxterm/issues/162))
- [`b021af6`](https://github.com/LeMyst/jmxterm/commit/b021af61f4a6d438023d2e14b8e30b4589443437) *(deps)* Bump docker/login-action from 3.5.0 to 3.6.0 ([#161](https://github.com/LeMyst/jmxterm/issues/161))
- [`313c289`](https://github.com/LeMyst/jmxterm/commit/313c2890406aaa63250cd8b3e478b9bf6289676e) *(deps)* Bump org.apache.commons:commons-lang3 from 3.18.0 to 3.19.0 ([#160](https://github.com/LeMyst/jmxterm/issues/160))
- [`58efa4d`](https://github.com/LeMyst/jmxterm/commit/58efa4db92eb24c49ccc1a82204141d3c247fdfb) *(deps)* Bump org.apache.maven.plugins:maven-compiler-plugin from 3.14.0 to 3.14.1 ([#159](https://github.com/LeMyst/jmxterm/issues/159))

### Others

- [`902b6fc`](https://github.com/LeMyst/jmxterm/commit/902b6fc00a1b85b5542840e57f072813cb46a62f) Remove code for JDK 5 and 6 ([#156](https://github.com/LeMyst/jmxterm/issues/156))
- [`d90061e`](https://github.com/LeMyst/jmxterm/commit/d90061ee8eca686778177aa8036264b4ca9ef8a5) Bump version to 1.0.12-SNAPSHOT in pom.xml ([#155](https://github.com/LeMyst/jmxterm/issues/155))

## [1.0.11](https://github.com/LeMyst/jmxterm/compare/v1.0.10..v1.0.11) - 2025-09-26

### ⚙️ Miscellaneous

- [`55d3144`](https://github.com/LeMyst/jmxterm/commit/55d31443cd3ac7c04bef6c0aa91d396f44a8a8e5) *(deps)* Bump com.google.guava:guava from 33.4.8-jre to 33.5.0-jre ([#151](https://github.com/LeMyst/jmxterm/issues/151))
- [`5e8cd5a`](https://github.com/LeMyst/jmxterm/commit/5e8cd5a6fcf4ae50a5810df2a65f9a51ed914a15) *(deps)* Bump org.apache.maven.plugins:maven-javadoc-plugin ([#150](https://github.com/LeMyst/jmxterm/issues/150))
- [`360826a`](https://github.com/LeMyst/jmxterm/commit/360826ab0ad4c81bae333a7997834ec785ef997a) *(deps)* Bump org.jline:jline from 3.30.5 to 3.30.6 ([#148](https://github.com/LeMyst/jmxterm/issues/148))
- [`f9bce21`](https://github.com/LeMyst/jmxterm/commit/f9bce21832c492980a59990e9194b9b669d1eb9e) *(deps)* Bump org.apache.maven.plugins:maven-surefire-plugin ([#147](https://github.com/LeMyst/jmxterm/issues/147))

### Others

- [`c13d9c0`](https://github.com/LeMyst/jmxterm/commit/c13d9c0a74dce18699f838233ea30ac11ee4d272) Update groupId and version to io.github.lemyst/1.0.11 ([#154](https://github.com/LeMyst/jmxterm/issues/154))
- [`444e0bf`](https://github.com/LeMyst/jmxterm/commit/444e0bfbafe92f5acb5369c03d109f8bc55891a1) Upgrade Alpine packages in Dockerfile ([#153](https://github.com/LeMyst/jmxterm/issues/153))
- [`4faba76`](https://github.com/LeMyst/jmxterm/commit/4faba761cbba6e5ab585b0dea15e5a4f9318034f) Clean POM file ([#149](https://github.com/LeMyst/jmxterm/issues/149))
- [`a04e5cc`](https://github.com/LeMyst/jmxterm/commit/a04e5cccda3fc81d5ee45cb2bd95b5a05fc6921b) Refine workflow permissions and conditional steps
- [`a8b4104`](https://github.com/LeMyst/jmxterm/commit/a8b41046de96d570498854dc34966a84067c07f4) Modify permissions in Maven CI workflow ([#152](https://github.com/LeMyst/jmxterm/issues/152))
- [`1c112fa`](https://github.com/LeMyst/jmxterm/commit/1c112fa27f5685a9d64c1122cdb65052d30a6c55) Add instructions for using GitHub Maven repository

## [1.0.10](https://github.com/LeMyst/jmxterm/compare/v1.0.4..v1.0.10) - 2025-09-08

### ⚙️ Miscellaneous

- [`21c463e`](https://github.com/LeMyst/jmxterm/commit/21c463e21c76962682505662c64fb3ee7e756b31) *(deps)* Bump actions/setup-java from 4 to 5 ([#144](https://github.com/LeMyst/jmxterm/issues/144))
- [`cd27242`](https://github.com/LeMyst/jmxterm/commit/cd272425e37899ff83c57afa994dd0d4d40537a2) Fix Docker build platforms to include arm64/v8 ([#142](https://github.com/LeMyst/jmxterm/issues/142))
- [`e506735`](https://github.com/LeMyst/jmxterm/commit/e506735e3e3ecfe2e74d61516b649d4f14f96c5a) Fix eclipse-temurin version in Dockerfile ([#141](https://github.com/LeMyst/jmxterm/issues/141))
- [`3fe8b3f`](https://github.com/LeMyst/jmxterm/commit/3fe8b3fb9e6a776f19567e3f6fc0effcc647e4b4) *(deps)* Bump actions/setup-java from 4 to 5 ([#140](https://github.com/LeMyst/jmxterm/issues/140))
- [`1c4c2cc`](https://github.com/LeMyst/jmxterm/commit/1c4c2ccafa324d074e6e7c896dcf9f42266bd046) *(deps)* Bump org.apache.maven.plugins:maven-javadoc-plugin ([#139](https://github.com/LeMyst/jmxterm/issues/139))
- [`78db533`](https://github.com/LeMyst/jmxterm/commit/78db533bc6e64cff46f7095a1329d5e444003ec4) *(deps)* Bump net.bytebuddy:byte-buddy from 1.17.6 to 1.17.7 ([#138](https://github.com/LeMyst/jmxterm/issues/138))
- [`4f5e8bc`](https://github.com/LeMyst/jmxterm/commit/4f5e8bc163e515dabffaa224c35cd6792fc7b63f) *(deps)* Bump actions/download-artifact from 4 to 5 ([#136](https://github.com/LeMyst/jmxterm/issues/136))
- [`f52c978`](https://github.com/LeMyst/jmxterm/commit/f52c9788c4592ab7fb547fda63c427dba22ce84d) *(deps)* Bump docker/metadata-action from 5.7.0 to 5.8.0 ([#135](https://github.com/LeMyst/jmxterm/issues/135))
- [`0df1db7`](https://github.com/LeMyst/jmxterm/commit/0df1db73296129c6473bf7270f79200281ab2098) *(deps)* Bump actions/checkout from 4 to 5 ([#134](https://github.com/LeMyst/jmxterm/issues/134))
- [`bbfc04d`](https://github.com/LeMyst/jmxterm/commit/bbfc04d126070bd55e2f37140d8ee1e6a83b4428) *(deps)* Bump docker/login-action from 3.4.0 to 3.5.0 ([#133](https://github.com/LeMyst/jmxterm/issues/133))
- [`6bc1a3c`](https://github.com/LeMyst/jmxterm/commit/6bc1a3c1922ec61e17746b395e7ee43632d2831e) *(deps)* Bump org.apache.commons:commons-text from 1.13.1 to 1.14.0 ([#132](https://github.com/LeMyst/jmxterm/issues/132))
- [`877978e`](https://github.com/LeMyst/jmxterm/commit/877978ec2cd0e9da8763e54cfdb534a0d1e0aac7) *(deps)* Bump eclipse-temurin ([#131](https://github.com/LeMyst/jmxterm/issues/131))
- [`3a33ea8`](https://github.com/LeMyst/jmxterm/commit/3a33ea86c27146a32e6858b3632970cda213d49a) *(deps)* Bump org.jline:jline from 3.30.4 to 3.30.5 ([#130](https://github.com/LeMyst/jmxterm/issues/130))
- [`30601a3`](https://github.com/LeMyst/jmxterm/commit/30601a3ce21a91a73b93991548d0f96a3b96f7a4) *(deps)* Bump commons-io:commons-io from 2.19.0 to 2.20.0 ([#129](https://github.com/LeMyst/jmxterm/issues/129))
- [`a326b76`](https://github.com/LeMyst/jmxterm/commit/a326b76830bdd3b7ac51a9b652d323c9fda096ba) *(deps)* Bump org.apache.commons:commons-lang3 in the maven group ([#128](https://github.com/LeMyst/jmxterm/issues/128))
- [`0358b0b`](https://github.com/LeMyst/jmxterm/commit/0358b0b9cb5013fe8018c024ee67b3ad055d6b66) *(deps)* Bump docker/setup-buildx-action from 3.11.0 to 3.11.1 ([#127](https://github.com/LeMyst/jmxterm/issues/127))
- [`499c56c`](https://github.com/LeMyst/jmxterm/commit/499c56c19db4ae975b57683deacc98d43caa9061) *(deps)* Bump docker/setup-buildx-action from 3.10.0 to 3.11.0 ([#126](https://github.com/LeMyst/jmxterm/issues/126))
- [`1f289b3`](https://github.com/LeMyst/jmxterm/commit/1f289b32e4b4eea96e181d86ce6398613e8f9ce7) *(deps)* Bump net.bytebuddy:byte-buddy from 1.17.5 to 1.17.6 ([#125](https://github.com/LeMyst/jmxterm/issues/125))
- [`b22b044`](https://github.com/LeMyst/jmxterm/commit/b22b04412fc727331996a839e2f3c97d653d1be5) *(deps)* Bump org.vafer:jdeb from 1.13 to 1.14 ([#124](https://github.com/LeMyst/jmxterm/issues/124))
- [`6f44050`](https://github.com/LeMyst/jmxterm/commit/6f44050336cfc4d7fa1f71422b5f2f4243232ed4) *(deps)* Bump com.google.guava:guava from 33.3.1-jre to 33.4.8-jre ([#123](https://github.com/LeMyst/jmxterm/issues/123))
- [`af6b8bb`](https://github.com/LeMyst/jmxterm/commit/af6b8bbfcd12db2764f5b393fbbc7d80a14aed59) *(deps)* Bump docker/build-push-action from 6.17.0 to 6.18.0 ([#122](https://github.com/LeMyst/jmxterm/issues/122))
- [`883f0c2`](https://github.com/LeMyst/jmxterm/commit/883f0c2b3bb37a01826f785d1911808d72a0ba26) *(deps)* Bump net.bytebuddy:byte-buddy from 1.14.11 to 1.17.5 ([#121](https://github.com/LeMyst/jmxterm/issues/121))
- [`d7e76a9`](https://github.com/LeMyst/jmxterm/commit/d7e76a94905b4beb5ff18e6f01b50ff9ad1b0978) *(deps)* Bump org.jline:jline from 3.30.3 to 3.30.4 ([#120](https://github.com/LeMyst/jmxterm/issues/120))
- [`908fad0`](https://github.com/LeMyst/jmxterm/commit/908fad0a3344a00f096946d401468e2ee894d477) *(deps)* Bump commons-beanutils:commons-beanutils in the maven group ([#119](https://github.com/LeMyst/jmxterm/issues/119))
- [`a52c052`](https://github.com/LeMyst/jmxterm/commit/a52c0521efd9c5157548ac735d9a601f60cd6553) *(deps)* Bump org.jline:jline from 3.30.2 to 3.30.3 ([#117](https://github.com/LeMyst/jmxterm/issues/117))
- [`5bd2c12`](https://github.com/LeMyst/jmxterm/commit/5bd2c12ce046b88b1e62c670e14e5f44cb987a65) *(deps)* Bump advanced-security/maven-dependency-submission-action ([#116](https://github.com/LeMyst/jmxterm/issues/116))
- [`6c0c4c0`](https://github.com/LeMyst/jmxterm/commit/6c0c4c011e651d5172ea4da14ff758a2e4be2176) *(deps)* Bump org.jline:jline from 3.30.0 to 3.30.2 ([#115](https://github.com/LeMyst/jmxterm/issues/115))
- [`a0e4d60`](https://github.com/LeMyst/jmxterm/commit/a0e4d6000e38962691ae64fe31865df8f757de15) *(deps)* Bump docker/build-push-action from 6.16.0 to 6.17.0 ([#114](https://github.com/LeMyst/jmxterm/issues/114))
- [`8f4dd1e`](https://github.com/LeMyst/jmxterm/commit/8f4dd1eb8b2eae4eeaed0974b9536c682f7a6f39) *(deps)* Bump org.jline:jline from 3.29.0 to 3.30.0 ([#113](https://github.com/LeMyst/jmxterm/issues/113))
- [`57b0436`](https://github.com/LeMyst/jmxterm/commit/57b043632eac58e1d5bd4c39c52a8316cfc24ab0) *(deps)* Bump eclipse-temurin ([#112](https://github.com/LeMyst/jmxterm/issues/112))
- [`212a4f2`](https://github.com/LeMyst/jmxterm/commit/212a4f2ecdfc5f9f59a6275f3a6208f1c6d52d67) *(deps)* Bump org.apache.commons:commons-collections4 ([#111](https://github.com/LeMyst/jmxterm/issues/111))
- [`9504f58`](https://github.com/LeMyst/jmxterm/commit/9504f58d9974b0777a1b5b529d06a93ff35d4bb5) *(deps)* Bump org.apache.commons:commons-configuration2 ([#110](https://github.com/LeMyst/jmxterm/issues/110))
- [`95dde34`](https://github.com/LeMyst/jmxterm/commit/95dde341a1d676d66d73bf4c3e3c23ad636b72cd) *(deps)* Bump docker/build-push-action from 6.15.0 to 6.16.0 ([#109](https://github.com/LeMyst/jmxterm/issues/109))
- [`7394d94`](https://github.com/LeMyst/jmxterm/commit/7394d949f9bdfeb685177549802fd3295ce06886) *(deps)* Bump com.google.guava:guava from 33.4.6-jre to 33.4.8-jre ([#108](https://github.com/LeMyst/jmxterm/issues/108))
- [`01a2fd5`](https://github.com/LeMyst/jmxterm/commit/01a2fd53b88d1a696235c8a26fd86f03b99b892d) *(deps)* Bump org.apache.commons:commons-text from 1.13.0 to 1.13.1 ([#107](https://github.com/LeMyst/jmxterm/issues/107))
- [`1dbe426`](https://github.com/LeMyst/jmxterm/commit/1dbe4266b6b09b30a0841bf471eb3fccb2ffcd40) *(deps)* Bump commons-io:commons-io from 2.18.0 to 2.19.0 ([#106](https://github.com/LeMyst/jmxterm/issues/106))
- [`e50f32a`](https://github.com/LeMyst/jmxterm/commit/e50f32a7585fb67cb2e14685039511c829ad7c8c) *(deps)* Bump eclipse-temurin ([#102](https://github.com/LeMyst/jmxterm/issues/102))
- [`9e7370b`](https://github.com/LeMyst/jmxterm/commit/9e7370b8548c7e446a54b55c1686332611d8d9b8) *(deps)* Bump org.apache.maven.plugins:maven-surefire-plugin ([#101](https://github.com/LeMyst/jmxterm/issues/101))
- [`c54c43a`](https://github.com/LeMyst/jmxterm/commit/c54c43a1af42273adbe907287094825c19b2773d) *(deps)* Bump com.google.guava:guava from 33.4.5-jre to 33.4.6-jre ([#100](https://github.com/LeMyst/jmxterm/issues/100))
- [`98c7447`](https://github.com/LeMyst/jmxterm/commit/98c74476c2f76123ca05d70cf29b09b14eeff6e3) *(deps)* Bump com.google.guava:guava from 33.4.0-jre to 33.4.5-jre ([#99](https://github.com/LeMyst/jmxterm/issues/99))
- [`bc66b12`](https://github.com/LeMyst/jmxterm/commit/bc66b12d3b1e962c6b227d22f9e9d48ad8512ad9) *(deps)* Bump docker/login-action from 3.3.0 to 3.4.0 ([#98](https://github.com/LeMyst/jmxterm/issues/98))
- [`1045237`](https://github.com/LeMyst/jmxterm/commit/1045237c01345200f23c0ae18d3bb3a716267032) *(deps)* Bump docker/metadata-action from 5.6.1 to 5.7.0 ([#95](https://github.com/LeMyst/jmxterm/issues/95))
- [`9f8064b`](https://github.com/LeMyst/jmxterm/commit/9f8064baa915dd78564495c86dcd4f255a2a220b) *(deps)* Bump docker/build-push-action from 6.14.0 to 6.15.0 ([#97](https://github.com/LeMyst/jmxterm/issues/97))
- [`d52d090`](https://github.com/LeMyst/jmxterm/commit/d52d0907d37fad9ccc3435852347ec38662107c2) *(deps)* Bump docker/setup-buildx-action from 3.9.0 to 3.10.0 ([#96](https://github.com/LeMyst/jmxterm/issues/96))
- [`9484182`](https://github.com/LeMyst/jmxterm/commit/9484182db98177ba84675be955124326ffa397ca) *(deps)* Bump docker/setup-qemu-action from 3.4.0 to 3.6.0 ([#94](https://github.com/LeMyst/jmxterm/issues/94))
- [`13d1998`](https://github.com/LeMyst/jmxterm/commit/13d1998704658275bf0cbcdea54b1378aa0e7281) *(deps)* Bump slf4j.version from 2.0.16 to 2.0.17 ([#93](https://github.com/LeMyst/jmxterm/issues/93))
- [`f4ada4d`](https://github.com/LeMyst/jmxterm/commit/f4ada4d3c4127e9209574d3773a4acf1d4ad9d83) *(deps)* Bump docker/build-push-action from 6.13.0 to 6.14.0 ([#92](https://github.com/LeMyst/jmxterm/issues/92))
- [`6918773`](https://github.com/LeMyst/jmxterm/commit/69187738db5c90e9e1e30a767ccaa19ef45ebfa8) *(deps)* Bump commons-beanutils:commons-beanutils ([#89](https://github.com/LeMyst/jmxterm/issues/89))
- [`0ce21fd`](https://github.com/LeMyst/jmxterm/commit/0ce21fd974c1ca637af4d2c164d4d315f7de8c41) *(deps)* Bump docker/setup-qemu-action from 3.3.0 to 3.4.0 ([#88](https://github.com/LeMyst/jmxterm/issues/88))
- [`db92db0`](https://github.com/LeMyst/jmxterm/commit/db92db0b4cc3c11c234965b2dcbc00f91d8a4fee) *(deps)* Bump docker/setup-buildx-action from 3.8.0 to 3.9.0 ([#87](https://github.com/LeMyst/jmxterm/issues/87))
- [`5c84917`](https://github.com/LeMyst/jmxterm/commit/5c84917cbc2e7f7cfc0065fdd985ce5b68f17a30) *(deps)* Bump eclipse-temurin ([#86](https://github.com/LeMyst/jmxterm/issues/86))
- [`cfbaffa`](https://github.com/LeMyst/jmxterm/commit/cfbaffa955463918f0a22335d93b3fa8f426c628) *(deps)* Bump org.jline:jline from 3.28.0 to 3.29.0 ([#85](https://github.com/LeMyst/jmxterm/issues/85))
- [`14a08d1`](https://github.com/LeMyst/jmxterm/commit/14a08d125ab68bded42139319890198c670ab1ad) *(deps)* Bump org.vafer:jdeb from 1.12 to 1.13 ([#84](https://github.com/LeMyst/jmxterm/issues/84))
- [`3573157`](https://github.com/LeMyst/jmxterm/commit/3573157daef6f5cb0550841f66c06b6ff9bb75cb) *(deps)* Bump docker/build-push-action from 6.12.0 to 6.13.0 ([#82](https://github.com/LeMyst/jmxterm/issues/82))
- [`2f2b972`](https://github.com/LeMyst/jmxterm/commit/2f2b9723028c831c1232afe15cf63addbb49aa6a) *(deps)* Bump docker/build-push-action from 6.11.0 to 6.12.0
- [`68fecb2`](https://github.com/LeMyst/jmxterm/commit/68fecb2d2297db566ce09b96e4408ab67d9944c5) *(deps)* Bump docker/build-push-action from 6.10.0 to 6.11.0
- [`8a0f2c1`](https://github.com/LeMyst/jmxterm/commit/8a0f2c1f804a13eef23628f86997977699925c4b) *(deps)* Bump docker/setup-qemu-action from 3.2.0 to 3.3.0
- [`d496eed`](https://github.com/LeMyst/jmxterm/commit/d496eed03858618d8ee0054c078a9cd50b1d163c) *(deps)* Bump commons-beanutils:commons-beanutils
- [`23360b0`](https://github.com/LeMyst/jmxterm/commit/23360b0594314f0350f4c31a835264edc99be774) *(deps)* Bump com.google.guava:guava from 33.3.1-jre to 33.4.0-jre
- [`56b0dd0`](https://github.com/LeMyst/jmxterm/commit/56b0dd067aed2e3e96194248ae1862495cb54564) *(deps)* Bump docker/setup-buildx-action from 3.7.1 to 3.8.0
- [`65b2b9f`](https://github.com/LeMyst/jmxterm/commit/65b2b9fa5d6b30bd715d1f1f2da00d1af2b6b0ef) *(deps)* Bump org.apache.commons:commons-text from 1.12.0 to 1.13.0
- [`b613a29`](https://github.com/LeMyst/jmxterm/commit/b613a298b2a12d1e7c6b7a87f11bde94ad66e54c) *(deps)* Bump org.jline:jline from 3.27.1 to 3.28.0
- [`b7d12f4`](https://github.com/LeMyst/jmxterm/commit/b7d12f4b8ab641bb2b1f7e933a8897d562d96be9) *(deps)* Bump org.apache.maven.plugins:maven-javadoc-plugin
- [`d4dd4f5`](https://github.com/LeMyst/jmxterm/commit/d4dd4f51abb69a5ff6c3aeb701b16163029898fc) *(deps)* Bump docker/build-push-action from 6.9.0 to 6.10.0
- [`8c876ab`](https://github.com/LeMyst/jmxterm/commit/8c876ab4af7ad52bbe7439a4fbf8665efedc1192) *(deps)* Bump org.vafer:jdeb from 1.11 to 1.12
- [`a97e376`](https://github.com/LeMyst/jmxterm/commit/a97e37646afcfcd93174c2068d1ec0c912d83e82) *(deps)* Bump commons-io:commons-io from 2.17.0 to 2.18.0
- [`f8c1c93`](https://github.com/LeMyst/jmxterm/commit/f8c1c9305e787bbf6b6da5ba6a8310cce88b2c89) *(deps)* Bump docker/metadata-action from 5.5.1 to 5.6.1
- [`e76c497`](https://github.com/LeMyst/jmxterm/commit/e76c4975d475a96392a2aeeeb02c60d29d9989a5) *(deps)* Bump org.apache.maven.plugins:maven-surefire-plugin
- [`f30b5c3`](https://github.com/LeMyst/jmxterm/commit/f30b5c392a4caf252ebb011a9d6e67654f15d63a) *(deps)* Bump org.apache.maven.plugins:maven-javadoc-plugin
- [`71c030d`](https://github.com/LeMyst/jmxterm/commit/71c030d541ee34c1c9382faf0edd0ab1962448f4) *(deps)* Bump eclipse-temurin
- [`7b9733b`](https://github.com/LeMyst/jmxterm/commit/7b9733b6c6715c44b1bdbe3087befdcfbd2bbea5) *(deps)* Bump org.jline:jline from 3.27.0 to 3.27.1
- [`4042afc`](https://github.com/LeMyst/jmxterm/commit/4042afc45d196aad5391432e9d9e807e28528580) *(deps)* Bump org.apache.maven.plugins:maven-surefire-plugin
- [`3194696`](https://github.com/LeMyst/jmxterm/commit/319469698a1aff3300bccf76ed6697e68b03efe9) *(deps)* Bump docker/setup-buildx-action from 3.7.0 to 3.7.1
- [`4347c8f`](https://github.com/LeMyst/jmxterm/commit/4347c8fb63bc396e8a53376d33c6170cc43d417c) *(deps)* Bump docker/setup-buildx-action from 3.6.1 to 3.7.0
- [`f56212c`](https://github.com/LeMyst/jmxterm/commit/f56212c5916f91823568b78d094201a801885200) *(deps)* Bump org.apache.maven.plugins:maven-javadoc-plugin
- [`c0f2ce5`](https://github.com/LeMyst/jmxterm/commit/c0f2ce511617281ba075a80fc1bdf99b4518e0a8) *(deps)* Bump docker/build-push-action from 6.8.0 to 6.9.0
- [`ba295a7`](https://github.com/LeMyst/jmxterm/commit/ba295a70638520d878e9db818237b85894aaa544) *(deps)* Bump docker/build-push-action from 6.7.0 to 6.8.0
- [`806bbb6`](https://github.com/LeMyst/jmxterm/commit/806bbb6747db39d6326a01c91e8d3d7d9765d1f5) *(deps)* Bump com.google.guava:guava from 33.3.0-jre to 33.3.1-jre
- [`10b63c0`](https://github.com/LeMyst/jmxterm/commit/10b63c0ce3aa74aec10c6c4a1d0b3f8d407892ef) *(deps)* Bump org.jline:jline from 3.26.3 to 3.27.0
- [`6714e87`](https://github.com/LeMyst/jmxterm/commit/6714e874f7cb4ef17a139128fb42d1364bea8e74) *(deps)* Bump eclipse-temurin
- [`53d8288`](https://github.com/LeMyst/jmxterm/commit/53d828840311e2077c15a9151ec5be6993845ba8) *(deps)* Bump commons-io:commons-io from 2.16.1 to 2.17.0
- [`d2b1e9c`](https://github.com/LeMyst/jmxterm/commit/d2b1e9c7deb67c24c6b42facb7ee2e9bf1793e6b) *(deps)* Bump org.apache.commons:commons-lang3 from 3.16.0 to 3.17.0
- [`25771d4`](https://github.com/LeMyst/jmxterm/commit/25771d41f759a47db5fa271b994f1cef6eba5b79) *(deps)* Bump org.apache.maven.plugins:maven-javadoc-plugin
- [`49f025f`](https://github.com/LeMyst/jmxterm/commit/49f025f358b4c76c494df57aae5d77f6603d08ce) *(deps)* Bump org.apache.maven.plugins:maven-surefire-plugin
- [`5ad66d1`](https://github.com/LeMyst/jmxterm/commit/5ad66d177d13c1eff97f1c38f441c0c2860be4d0) *(deps)* Bump org.apache.maven.plugins:maven-surefire-plugin
- [`8a3df00`](https://github.com/LeMyst/jmxterm/commit/8a3df006ed23533cfca5dd08d95a03a197d978a4) *(deps)* Bump org.vafer:jdeb from 1.10 to 1.11
- [`362427b`](https://github.com/LeMyst/jmxterm/commit/362427b1ee596b6681e8ca00ee74036ac9fad5b3) *(deps)* Bump com.google.guava:guava from 33.2.1-jre to 33.3.0-jre
- [`8c9be05`](https://github.com/LeMyst/jmxterm/commit/8c9be05e56eb39f99dc38cccf58182f4b74f47a8) *(deps)* Bump docker/build-push-action from 6.6.1 to 6.7.0
- [`fc477cf`](https://github.com/LeMyst/jmxterm/commit/fc477cf667ff4b4bf0f82d0301120f814dd63d3c) *(deps)* Bump slf4j.version from 2.0.15 to 2.0.16
- [`8d879f0`](https://github.com/LeMyst/jmxterm/commit/8d879f0d77f2763274204feb6342ab5bc8991c06) *(deps)* Bump slf4j.version from 2.0.14 to 2.0.15
- [`16a31d6`](https://github.com/LeMyst/jmxterm/commit/16a31d60eb82a3fa8ad1d9458bfb5a2aebe9d0ac) *(deps)* Bump docker/build-push-action from 6.6.0 to 6.6.1
- [`8eabfbe`](https://github.com/LeMyst/jmxterm/commit/8eabfbebbfa1bf312c1d8427e6a0f0cdef48dbb9) *(deps)* Bump slf4j.version from 2.0.13 to 2.0.14
- [`7597787`](https://github.com/LeMyst/jmxterm/commit/7597787241abbb75230e2f98bf328c52bba4ed4a) *(deps)* Bump org.apache.commons:commons-lang3 from 3.15.0 to 3.16.0
- [`ec68efb`](https://github.com/LeMyst/jmxterm/commit/ec68efb21c787b1746d6772c31f679b9f35826ca) *(deps)* Bump docker/build-push-action from 6.5.0 to 6.6.0
- [`d949e63`](https://github.com/LeMyst/jmxterm/commit/d949e63540aac5db6ee65f609aa1f8a5b3ddc277) *(deps)* Bump docker/setup-buildx-action from 3.5.0 to 3.6.1
- [`0478b7f`](https://github.com/LeMyst/jmxterm/commit/0478b7f2a75e8be4c12c22ceaf2365a5b2ac77f5) *(deps)* Bump eclipse-temurin
- [`4a066b5`](https://github.com/LeMyst/jmxterm/commit/4a066b570bc90f34e24f34e5469e0f012df1deb8) *(deps)* Bump org.apache.maven.plugins:maven-javadoc-plugin
- [`5706df6`](https://github.com/LeMyst/jmxterm/commit/5706df692c24277ba10f4fac1f4da3e2fb2ff39f) *(deps)* Bump docker/setup-qemu-action from 3.1.0 to 3.2.0
- [`dbf2dd5`](https://github.com/LeMyst/jmxterm/commit/dbf2dd58cd05e1355f47f16cd771e42179526b1c) *(deps)* Bump docker/login-action from 3.2.0 to 3.3.0
- [`f0ad448`](https://github.com/LeMyst/jmxterm/commit/f0ad448f34253673eea6c78c16e3484fa496d35c) *(deps)* Bump docker/setup-buildx-action from 3.4.0 to 3.5.0
- [`82570d9`](https://github.com/LeMyst/jmxterm/commit/82570d9cd2e3c63103361a4fa27419b608cba4dd) *(deps)* Bump docker/build-push-action from 6.4.1 to 6.5.0
- [`37fce10`](https://github.com/LeMyst/jmxterm/commit/37fce104b792b6e6e2f0f17d9a2dcada3d02ae4c) *(deps)* Bump org.apache.commons:commons-lang3 from 3.14.0 to 3.15.0
- [`de6dccb`](https://github.com/LeMyst/jmxterm/commit/de6dccb9d74ddf6d008893004726c2df4aadd397) *(deps)* Bump org.jline:jline from 3.26.2 to 3.26.3
- [`b3defb3`](https://github.com/LeMyst/jmxterm/commit/b3defb3b23f08fb629719fe365e501869c0cfe24) *(deps)* Bump docker/build-push-action from 6.4.0 to 6.4.1
- [`72c3829`](https://github.com/LeMyst/jmxterm/commit/72c3829b1cdde928d58cedf24ab10e192e44ecc3) *(deps)* Bump org.apache.maven.plugins:maven-surefire-plugin
- [`44e2661`](https://github.com/LeMyst/jmxterm/commit/44e2661dc95b02a62c9d31b5298d25538e0ffb2e) *(deps)* Bump docker/build-push-action from 6.3.0 to 6.4.0
- [`66f3852`](https://github.com/LeMyst/jmxterm/commit/66f38528b02bc8a3b27d1237d820fb62ea236f0e) *(deps)* Bump docker/setup-buildx-action from 3.3.0 to 3.4.0
- [`7f18904`](https://github.com/LeMyst/jmxterm/commit/7f189043e3b18dfa3ef04d50ed7129c9afee8d58) *(deps)* Bump docker/build-push-action from 6.2.0 to 6.3.0
- [`79b37a5`](https://github.com/LeMyst/jmxterm/commit/79b37a50a1ebbe1eb3abf9251eaed2a5beb9a1e4) *(deps)* Bump docker/setup-qemu-action from 3.0.0 to 3.1.0
- [`0a06161`](https://github.com/LeMyst/jmxterm/commit/0a0616131d5722d6ab1a697a67e10fb8ac0b9283) *(deps)* Bump docker/build-push-action from 6.1.0 to 6.2.0
- [`3bc16b5`](https://github.com/LeMyst/jmxterm/commit/3bc16b5301147f4e4db9b39fc66a39281c4c5c03) *(deps)* Bump org.apache.maven.plugins:maven-jar-plugin
- [`cdd31d1`](https://github.com/LeMyst/jmxterm/commit/cdd31d10f0fa7d79902ba3f625bb1ee04648d2bf) *(deps)* Bump docker/build-push-action from 6.0.1 to 6.1.0
- [`c7d7075`](https://github.com/LeMyst/jmxterm/commit/c7d707543e71bafab54575efce3150c786eb1127) *(deps)* Bump eclipse-temurin
- [`3b2c83a`](https://github.com/LeMyst/jmxterm/commit/3b2c83a4ac45eae515cc9a35d029defcbb1014d0) *(deps)* Bump docker/build-push-action from 6.0.0 to 6.0.1
- [`d4b02f6`](https://github.com/LeMyst/jmxterm/commit/d4b02f64de3746e21dfeb69593a8f81a37d47262) *(deps)* Bump org.apache.maven.plugins:maven-source-plugin
- [`c66af33`](https://github.com/LeMyst/jmxterm/commit/c66af33d58b0e50481ddbd155471e9ed3055203f) *(deps)* Bump actions/upload-artifact from 2 to 4
- [`c665827`](https://github.com/LeMyst/jmxterm/commit/c66582734b97cff202c706e0a240313b96009bbb) *(deps)* Bump org.apache.commons:commons-text from 1.10.0 to 1.12.0
- [`20a3e95`](https://github.com/LeMyst/jmxterm/commit/20a3e95910e50f1fe5f50e220f289862b8fcf8f9) *(deps)* Bump org.apache.commons:commons-lang3 from 3.8.1 to 3.14.0
- [`6b1897c`](https://github.com/LeMyst/jmxterm/commit/6b1897c5c4d44a3c35942732fbe865248436e986) *(deps)* Bump slf4j.version from 1.7.25 to 2.0.13
- [`0ad941e`](https://github.com/LeMyst/jmxterm/commit/0ad941e2c00068fecf7da0f8fd32d0e01b69fa42) *(deps)* Bump commons-io:commons-io from 2.7 to 2.16.1
- [`0be1306`](https://github.com/LeMyst/jmxterm/commit/0be1306902ece52b637fe1f2fdd3067f81c156ec) *(deps)* Bump junit:junit from 4.13.1 to 4.13.2
- [`e01abf4`](https://github.com/LeMyst/jmxterm/commit/e01abf4cab49cec90089a23b66f04c44028c3f4a) *(deps)* Bump org.apache.maven.plugins:maven-javadoc-plugin
- [`ceac436`](https://github.com/LeMyst/jmxterm/commit/ceac436e098510d6a1d2a05acd3ac67189dec33b) *(deps)* Bump org.apache.maven.plugins:maven-jar-plugin
- [`7624160`](https://github.com/LeMyst/jmxterm/commit/762416016635cef56ae704813d623080de3ed33c) *(deps)* Bump org.vafer:jdeb from 1.6 to 1.10
- [`9b96d02`](https://github.com/LeMyst/jmxterm/commit/9b96d02c46135946e8c2dc59459334c65c4f124e) *(deps)* Bump jmock.version from 2.8.3 to 2.13.1
- [`2b5b75a`](https://github.com/LeMyst/jmxterm/commit/2b5b75a080e551165106d2a6419cc2a2652018c4) *(deps)* Bump actions/setup-java from 3 to 4

### Others

- [`5194cba`](https://github.com/LeMyst/jmxterm/commit/5194cba0cdbdcaf099e757dcdbfe7ac17950283f) Bump version to 1.0.10-lemyst in pom.xml
- [`ab7a898`](https://github.com/LeMyst/jmxterm/commit/ab7a898350ec2388e7dedb85cf2287d7639bd679) Implement Maven GitHub Packages publishing for automated release distribution ([#145](https://github.com/LeMyst/jmxterm/issues/145))
- [`f2b517b`](https://github.com/LeMyst/jmxterm/commit/f2b517b8151ff08d16343d6213f2f0cd37818d83) Revert "Add permissions for security events and id-token"
- [`9cd2d5c`](https://github.com/LeMyst/jmxterm/commit/9cd2d5c259fd3a227f5e98dfd92223eba711bade) Properly cast vm object ([#146](https://github.com/LeMyst/jmxterm/issues/146))
- [`7f9e6be`](https://github.com/LeMyst/jmxterm/commit/7f9e6bedd86225755c4ab51a10fe1bbf35f3214d) Add permissions for security events and id-token
- [`0014578`](https://github.com/LeMyst/jmxterm/commit/00145783969dba5f23aa10487759d450af414d86) Improve maven tests workflow ([#137](https://github.com/LeMyst/jmxterm/issues/137))
- [`b957d1d`](https://github.com/LeMyst/jmxterm/commit/b957d1d88952363aab636f9eba8f104b126d1c8e) Potential fix for code scanning alert no. 35: Workflow does not contain permissions ([#118](https://github.com/LeMyst/jmxterm/issues/118))
- [`66ce2cc`](https://github.com/LeMyst/jmxterm/commit/66ce2cc18fc6f49688fb953d1dd51e34d2993673) Fix release workflow ([#105](https://github.com/LeMyst/jmxterm/issues/105))
- [`6b717d6`](https://github.com/LeMyst/jmxterm/commit/6b717d64f246b5d10d197af14248fa7c81fb3ef6) Prepare v1.0.9 ([#104](https://github.com/LeMyst/jmxterm/issues/104))
- [`32d8249`](https://github.com/LeMyst/jmxterm/commit/32d8249230b8a31ceba06725132df86fe36d2f5a) Improve workflows ([#103](https://github.com/LeMyst/jmxterm/issues/103))
- [`673912a`](https://github.com/LeMyst/jmxterm/commit/673912a15f3daa0dafa4bc3f5d06e26711cb43dc) Fix nightly build
- [`80969f5`](https://github.com/LeMyst/jmxterm/commit/80969f5b29c5275cdcb1cd4c1b51d784bf4b2114) Change dependabot frequency ([#91](https://github.com/LeMyst/jmxterm/issues/91))
- [`0f41f33`](https://github.com/LeMyst/jmxterm/commit/0f41f3329d674f9415793970d36ad67c50fed229) Create nightly-build.yml ([#90](https://github.com/LeMyst/jmxterm/issues/90))
- [`ab78842`](https://github.com/LeMyst/jmxterm/commit/ab78842bbb91bc4b48938da0a4581ce81501566c) Dependabot automerge ([#83](https://github.com/LeMyst/jmxterm/issues/83))
- [`00c8614`](https://github.com/LeMyst/jmxterm/commit/00c861428d1f2fc5d08e459a5eec31b75edd7823) Change trivy image repository to AWS ECR
- [`f6cc0a0`](https://github.com/LeMyst/jmxterm/commit/f6cc0a03af151d3eca245b539c1b7ef6bb71f672) Bump to 1.0.8-lemyst
- [`23265f4`](https://github.com/LeMyst/jmxterm/commit/23265f47fb27d24247d839c2c9fb476dc8c49b5f) Bump to 1.0.7-lemyst
- [`4d9ff5b`](https://github.com/LeMyst/jmxterm/commit/4d9ff5be5d0fa18b58453a90257b8b900cb40898) Add JRE message
- [`8f1ef5a`](https://github.com/LeMyst/jmxterm/commit/8f1ef5a5eb556faad5e7d2256da3b9a8b551d162) Update create-release.yaml
- [`bd44dc4`](https://github.com/LeMyst/jmxterm/commit/bd44dc4d5a202d36b4b914c4a6040f363a484ed6) Prepare for v1.0.6
- [`2a66ec6`](https://github.com/LeMyst/jmxterm/commit/2a66ec681db9d565c7ca29a92637a99cecbbd67e) Add changelog to release
- [`664fac4`](https://github.com/LeMyst/jmxterm/commit/664fac4b3b12845b824a9acbf8996fa0fa2e81db) Delete Vagrantfile
- [`66a22d2`](https://github.com/LeMyst/jmxterm/commit/66a22d28297621766086e959dad03022cd0103d1) Remove linux/arm/v7
- [`8494d02`](https://github.com/LeMyst/jmxterm/commit/8494d02fd353f633669872cfc1d3445cab93f0bf) Update Dockerfile
- [`df77a88`](https://github.com/LeMyst/jmxterm/commit/df77a88ef52b494b6f6248aa05bfdba9aecfc8cf) Update workflows
- [`e4881c7`](https://github.com/LeMyst/jmxterm/commit/e4881c71870d42f72c2a518ab6188b884dbc5c25) Add release workflow
- [`825cb5a`](https://github.com/LeMyst/jmxterm/commit/825cb5a2b4f038373438f7e53f900d9dda47966f) Improve pom.xml
- [`0abdb8b`](https://github.com/LeMyst/jmxterm/commit/0abdb8b1c37dd140549b681794a45a416d77f892) Disable for dependabot
- [`25757b6`](https://github.com/LeMyst/jmxterm/commit/25757b60ea6fdc8a23e285788217ccccb74da30a) Try to fix dependabot error
- [`cd7e83e`](https://github.com/LeMyst/jmxterm/commit/cd7e83e835dfa2d14aa0e91efb88d666927993c2) Update dependency graph version
- [`34bb591`](https://github.com/LeMyst/jmxterm/commit/34bb591e935e2c342a302406c13d96b8336e5581) Update workflows
- [`4645fe4`](https://github.com/LeMyst/jmxterm/commit/4645fe490479192ab7eae90cfd2ea54b6c38fcdd) Use java 11 for maven workflow
- [`7da3128`](https://github.com/LeMyst/jmxterm/commit/7da31285b00e3fac8a52998066b1d789b59407df) Create maven github workflows
- [`d51fbde`](https://github.com/LeMyst/jmxterm/commit/d51fbdeaa9b5322052b929b76d3c4784c2ec736d) Avoid usage of jdk.jconsole module in Java 9+ ([#113](https://github.com/LeMyst/jmxterm/issues/113))
- [`13d3706`](https://github.com/LeMyst/jmxterm/commit/13d3706a0d4a6dd88da799bca91c4cf102d480a4) Replace jmock-legacy by jmock-imposters ([#112](https://github.com/LeMyst/jmxterm/issues/112))
- [`351e9bc`](https://github.com/LeMyst/jmxterm/commit/351e9bc6bf8bd3700600619824c4ebe05b5ea7ef) Release 1.0.5 passes SonatypeIQ security scan ([#121](https://github.com/LeMyst/jmxterm/issues/121))
- [`9d9cc7c`](https://github.com/LeMyst/jmxterm/commit/9d9cc7c29ee00b87e7f6e43acbcf7c5cfb0ee2ab) Bump commons-io:commons-io from 2.7 to 2.14.0 ([#122](https://github.com/LeMyst/jmxterm/issues/122))
- [`2b6939a`](https://github.com/LeMyst/jmxterm/commit/2b6939a9a4b5896473b72e2d493f0cc3bfaa2d14) Bump org.apache.commons:commons-configuration2 from 2.8.0 to 2.10.1 ([#114](https://github.com/LeMyst/jmxterm/issues/114))
- [`506795b`](https://github.com/LeMyst/jmxterm/commit/506795bbf0ff09e4bd7e2a578b8bfcbe82d3e2b0) Fix for mvn site command ([#110](https://github.com/LeMyst/jmxterm/issues/110))

## [1.0.4](https://github.com/jiaqi/jmxterm/compare/v1.0.3..v1.0.4) - 2022-10-27

### Others

- [`855e1c9`](https://github.com/jiaqi/jmxterm/commit/855e1c9cb9d1a3b1435ba34188169bb55019cae6) Update for 1.0.4 release ([#107](https://github.com/jiaqi/jmxterm/issues/107))
- [`778eb8f`](https://github.com/jiaqi/jmxterm/commit/778eb8f5ddbfb596ef3c90b2873d30a37c16e3ce) Address https://nvd.nist.gov/vuln/detail/CVE-2022-42889 by upgrading org.apache.commons:commons-text to version 1.10.0 (from 1.9) ([#105](https://github.com/jiaqi/jmxterm/issues/105))
- [`95cd540`](https://github.com/jiaqi/jmxterm/commit/95cd5400255bb94f8198ebe8ae9da26623444524) Change version back to snapshot

## [1.0.3](https://github.com/jiaqi/jmxterm/compare/v1.0.2..v1.0.3) - 2022-08-06

### Others

- [`62504b9`](https://github.com/jiaqi/jmxterm/commit/62504b90fac047485743107b48180833b423743d) Set version to 1.0.3 to perform a release
- [`efaf279`](https://github.com/jiaqi/jmxterm/commit/efaf2792d9d2d009753b5ee0f8b6b9d570157279) Tighen javadoc checks
- [`0f82c98`](https://github.com/jiaqi/jmxterm/commit/0f82c984e5abe62c8249b14c73a27075a8655813) Upgrade org.apache.commons:commons-configuration2 to version 2.8.0 to address https://www.cve.org/CVERecord?id=CVE-2022-33980 ([#103](https://github.com/jiaqi/jmxterm/issues/103))
- [`cfe6223`](https://github.com/jiaqi/jmxterm/commit/cfe6223799bbb223b1e4386c9b22962aff22fc03) Improve Travis CI build Performance ([#99](https://github.com/jiaqi/jmxterm/issues/99))
- [`8a8a549`](https://github.com/jiaqi/jmxterm/commit/8a8a549e9c69d5b822f8e1c32692048792e346af) Bump commons-configuration2 from 2.7 to 2.8.0 ([#102](https://github.com/jiaqi/jmxterm/issues/102))
- [`b29cd07`](https://github.com/jiaqi/jmxterm/commit/b29cd07192c3c978d118c0f17245b88720f92000) Bump commons-io from 2.6 to 2.7 ([#97](https://github.com/jiaqi/jmxterm/issues/97))
- [`9957860`](https://github.com/jiaqi/jmxterm/commit/995786063bd53ca32f6a51b6b569d090c2f02082) Continue on exceptions when showing bean attrs ([#98](https://github.com/jiaqi/jmxterm/issues/98))
- [`edadc72`](https://github.com/jiaqi/jmxterm/commit/edadc727b1a3c4b0872324f9ed107b4a7cfbbe35) Add workflow dependency
- [`614ff7c`](https://github.com/jiaqi/jmxterm/commit/614ff7cb503ca95ad946c0d559c0dd306ee1e374) Fix deploy CI ([#90](https://github.com/jiaqi/jmxterm/issues/90))
- [`f359f97`](https://github.com/jiaqi/jmxterm/commit/f359f973c2c1f3eb35bdb7d4a0bc7528c8b822b6) Add back working directory param
- [`f8afb15`](https://github.com/jiaqi/jmxterm/commit/f8afb157970a8c52565ac1586d5d71e48b0715ef) Deploy snapshot only for master build ([#89](https://github.com/jiaqi/jmxterm/issues/89))
- [`61606c2`](https://github.com/jiaqi/jmxterm/commit/61606c212d2c0d262a581a944e4df127866e2926) Restore the head version to snapshot

## [1.0.2](https://github.com/jiaqi/jmxterm/compare/v1.0.1..v1.0.2) - 2020-09-04

### Others

- [`5c13c7b`](https://github.com/jiaqi/jmxterm/commit/5c13c7be6124e9d50026b202b2bc5a5fa622659f) Prepare for 1.0.2 release
- [`d58fac1`](https://github.com/jiaqi/jmxterm/commit/d58fac180bfc0f126cc7d6fed3105e0c141c4ccb) Order the dependencies
- [`7cb8f02`](https://github.com/jiaqi/jmxterm/commit/7cb8f023cb8cce3a17338e938bf766c7114d1b22) Upgrade beanutils to 1.9.4
- [`ce0008b`](https://github.com/jiaqi/jmxterm/commit/ce0008be45a586b6cd7ff48fc279b346189beba3) Bump commons-configuration2 and -text versions ([#83](https://github.com/jiaqi/jmxterm/issues/83))
- [`334d12b`](https://github.com/jiaqi/jmxterm/commit/334d12bd3bcb8a0be5c8d435a259705c22dafa99) Handle 30x redirects and switch to downloads.apache.org ([#80](https://github.com/jiaqi/jmxterm/issues/80))
- [`75f5288`](https://github.com/jiaqi/jmxterm/commit/75f5288b06b9cb88c62d881296060f511f015fc1) Upgrade Maven to 3.6.3 for Docker build
- [`b227031`](https://github.com/jiaqi/jmxterm/commit/b2270314bed933be5c8f1a0ebb096a086adb4a4e) Fix javadoc syntax issues
- [`905de53`](https://github.com/jiaqi/jmxterm/commit/905de53eef47382420e11fde2cb12c40a097e72b) Pull 74 with merge conflict resolved
- [`c6a0ae5`](https://github.com/jiaqi/jmxterm/commit/c6a0ae5c6c5b3141e5c0207f44d4757919f94696) Pull 75 with conflict resolved.
- [`07635f9`](https://github.com/jiaqi/jmxterm/commit/07635f95d926c7b117841327bb8aaa5c2a013800) Info -e display description from instance of the mxbean, description of parameters.
- [`63863a7`](https://github.com/jiaqi/jmxterm/commit/63863a7c3d18c62275713889ff5cd6d9299fe3f4) Upgrade parent pom from 0.7.1 to 0.7.2
- [`222aeb1`](https://github.com/jiaqi/jmxterm/commit/222aeb1f9d89a339caabb9884c577676955e678e) Reformat code with Google format
- [`c983134`](https://github.com/jiaqi/jmxterm/commit/c983134958bcda27476553723c11103adf957e9d) Upgrade pom
- [`e2606d2`](https://github.com/jiaqi/jmxterm/commit/e2606d2245010e27ca3aa184e6a3820cc2be140b) Upgrade parent pom
- [`f39edc3`](https://github.com/jiaqi/jmxterm/commit/f39edc3e482101dd0c4fcfceb55134702ca2243e) Enable auto deploy
- [`32e9cae`](https://github.com/jiaqi/jmxterm/commit/32e9cae8139cd28d9199d0365d6f433a6f2cc39f) Remove some redundant plugin versions from pom.xml
- [`0208715`](https://github.com/jiaqi/jmxterm/commit/0208715ad8d24fc9ad298a1540f4a007aace958e) Add a Dockerfile to run jmxterm
- [`5194466`](https://github.com/jiaqi/jmxterm/commit/5194466e23c94a505cb51f11ca7de8c5187d48ed) Try fixing travis build error
- [`e2ee8f9`](https://github.com/jiaqi/jmxterm/commit/e2ee8f91cf288fcd52b45c8d695de14ac8708224) Add circleci configuration file
- [`9a9a0a1`](https://github.com/jiaqi/jmxterm/commit/9a9a0a1203e3a4054556bbc24844247303ec4f0e) Disconnect jmx when terminating jmxterm

## [1.0.1](https://github.com/jiaqi/jmxterm/compare/v1.0.0..v1.0.1) - 2019-05-01

### 🐛 Bug Fixes

- [`f559b36`](https://github.com/jiaqi/jmxterm/commit/f559b369dbecc01936f47d81fd27801f6a1c60b9) Fixed java9+ support

### Others

- [`0c3e699`](https://github.com/jiaqi/jmxterm/commit/0c3e6990eb60d990ad33fd6fdc634f039c8d1daf) Setting version to 1.0.1 for the next release
- [`5211459`](https://github.com/jiaqi/jmxterm/commit/52114598b9c1dbfe8a56942f06ac38da2f48366f) Make slf4j-simple runtime dependency
- [`ab457c4`](https://github.com/jiaqi/jmxterm/commit/ab457c43802b81dd0bef9a5d7bccf58543c11fb2) Issue 61 - Add in slf4j-simple to remove SLF4j logging warnings
- [`0854f67`](https://github.com/jiaqi/jmxterm/commit/0854f6779beb5499576f1b15fee312d2cfedabd7) Issue 54 (Declare RPM is not arch-specific and target OS is Linux, to allow the RPM to be built on OSX via Maven and generate a compatible RPM to use on Linux.
- [`4fdb700`](https://github.com/jiaqi/jmxterm/commit/4fdb7003e113df0f93b0418023d0ab1b7866409b) Clear some compile time warnings
- [`a6507c6`](https://github.com/jiaqi/jmxterm/commit/a6507c62afcf849d2ecfbbfcf70e27aaf0b5696a) [ISSUE 51] Add support for (m)TLS
- [`2e1f4f8`](https://github.com/jiaqi/jmxterm/commit/2e1f4f89cd136531dde8ca8dda883fc78b4f80ea) Exclude /usr/bin
- [`fff235a`](https://github.com/jiaqi/jmxterm/commit/fff235a5cc4f466f345c950a9a136bda65da9637) Use released 1.0.1 version of jcli
- [`a7c890f`](https://github.com/jiaqi/jmxterm/commit/a7c890f82d60bdfa1943992eedb4de08bfcf4f84) Pint to the snapshot version of jcli
- [`2423451`](https://github.com/jiaqi/jmxterm/commit/2423451a2740ff6356f5e016634c7f78beb47d14) Format pom.xml and add explicity license definition
- [`1c28fa2`](https://github.com/jiaqi/jmxterm/commit/1c28fa25120b3736e4fd0907d77ba983003fe940) Refer to documentation at https://docs.cyclopsgroup.org/jmxterm
- [`74ebeef`](https://github.com/jiaqi/jmxterm/commit/74ebeef076b841ab7060babc52c3c2f1f596dfc0) Merge branch 'master' of github.com:jiaqi/jmxterm
- [`55dca9f`](https://github.com/jiaqi/jmxterm/commit/55dca9f2ec82246e6d7c573010a55af874f39bc4) Upgrading commons-lang3 to fix runtime crash in jre 11
- [`8224d33`](https://github.com/jiaqi/jmxterm/commit/8224d33f6b97f247ce5b7b5f8faf97996e5a88fe) Update .travis.yml
- [`c574fd2`](https://github.com/jiaqi/jmxterm/commit/c574fd222c1e666225953ae4fdc4b494794cd825) Update pom.xml
- [`5e5d9cb`](https://github.com/jiaqi/jmxterm/commit/5e5d9cb10f1e96c04b62ef987f5d5e6e57d4c880) Try adding MD files to docs
- [`8836419`](https://github.com/jiaqi/jmxterm/commit/883641991a87c1450f28f9fc1c269e544d4815a2) Update site and travis
- [`e005140`](https://github.com/jiaqi/jmxterm/commit/e0051409d94a8f42836a472cdf8a88878ddb878d) Update documents
- [`ed52581`](https://github.com/jiaqi/jmxterm/commit/ed52581e5afcf927681739f3cd6ae5b535deb52c) Update README.md
- [`11555f0`](https://github.com/jiaqi/jmxterm/commit/11555f06e2de580e74cb63e5dfe79e6487841016) Update README.md
- [`8bcc285`](https://github.com/jiaqi/jmxterm/commit/8bcc285bceb1027804cd29f283af14eadbea0025) Update README.md
- [`74946ef`](https://github.com/jiaqi/jmxterm/commit/74946efc7c634984d6383c395b07f7980cf9d0a8) Merge https://github.com/jiaqi/jmxterm/pull/47
- [`4456777`](https://github.com/jiaqi/jmxterm/commit/445677750284cefdf7638068cd2734130681bbd7) Rollback the change of version
- [`5439655`](https://github.com/jiaqi/jmxterm/commit/5439655d86e13ff930f7424ca94551d69637fc3b) Mentioning history file problems in README
- [`2d4f1a5`](https://github.com/jiaqi/jmxterm/commit/2d4f1a58019ad4c502b12c8b8696f034b06a8a5b) Using version 2.0.0 due to incompatibilities in history format
- [`dc31cfb`](https://github.com/jiaqi/jmxterm/commit/dc31cfb57697f6b2bf91da64fb7ea45224391bed) Create LICENSE
- [`d8067c5`](https://github.com/jiaqi/jmxterm/commit/d8067c52da35e1f710e10f80d2c971ac29a7f316) Format code with Google style
- [`be0eef6`](https://github.com/jiaqi/jmxterm/commit/be0eef6e31252efb11ed82ccdf230fb2ce0b4ba9) Fix some Javadoc errors and warnings
- [`90073fa`](https://github.com/jiaqi/jmxterm/commit/90073fa9a2f2c7c6ca2c6a8bfc69b81a8fe5d524) Set next version to 1.0.1
- [`ed5eb6f`](https://github.com/jiaqi/jmxterm/commit/ed5eb6f7e0b18a03a4a2e627872740beeb87dee2) Merge from pull/46
- [`a9ef45a`](https://github.com/jiaqi/jmxterm/commit/a9ef45aea8e34138f7f80344d125cac72a5c6730) - Using version 2.0.0 due to incompatibilities in history format
- [`eb38f40`](https://github.com/jiaqi/jmxterm/commit/eb38f4012dacafe4b4e876004ccf09080bde4e8a) - Replaced unmaintained classworlds with Maven assembly
- [`211a817`](https://github.com/jiaqi/jmxterm/commit/211a81796b512118b338f16231de0cf748e0b673) Reformat pom.xml
- [`3134334`](https://github.com/jiaqi/jmxterm/commit/3134334bc514707c93e163051c73ae1a5b0b0600) Fixed javadoc errors
- [`5063e8b`](https://github.com/jiaqi/jmxterm/commit/5063e8b27200b571238e18077b91b3eb1439fd0d) Updated dependency versions
- [`20a6820`](https://github.com/jiaqi/jmxterm/commit/20a682019a82c2ee6094d5a7f9995bada7038371) Removed redundant group id
- [`3281e8a`](https://github.com/jiaqi/jmxterm/commit/3281e8a5ac369327d80e53f7e5505d37eaa5a059) Excluded IDEA metadata from SCM
- [`8f47748`](https://github.com/jiaqi/jmxterm/commit/8f4774860f89d2c4ce160de1d62018c17e34c581) Fix tests under windows
- [`32cf434`](https://github.com/jiaqi/jmxterm/commit/32cf434df4d70cd02503e60d3be65b9156e2f7d7) Fixed javadoc errors
- [`b38ce43`](https://github.com/jiaqi/jmxterm/commit/b38ce43e43cc5a8048eb31e8f56939a812068796) Updated dependency versions
- [`8bd352f`](https://github.com/jiaqi/jmxterm/commit/8bd352fd52c3ea5621ae784f0f0a0e8b10c2a3a0) Removed redundant group id
- [`47955b4`](https://github.com/jiaqi/jmxterm/commit/47955b4fae9339a92f30b339a0bc6ea0d50e88b0) Excluded IDEA metadata from SCM

## 1.0.0 - 2017-07-28

### Others

- [`67f6ff4`](https://github.com/jiaqi/jmxterm/commit/67f6ff4adc7d3199fbc7848503b385282ceed67a) Get ready for 1.0.0 release
- [`385df87`](https://github.com/jiaqi/jmxterm/commit/385df87f5d58eb54dd2c2e6eb29cf27626b31d39) Merge pull request [#39](https://github.com/jiaqi/jmxterm/issues/39) from wesamhaboush/master
- [`379ba64`](https://github.com/jiaqi/jmxterm/commit/379ba643a43125f5523e370814f0579522696971) Add comment char escape capability
- [`6657bd3`](https://github.com/jiaqi/jmxterm/commit/6657bd3ab60032d8507ec7c27a98a1a22c6bb268) Merge pull request [#21](https://github.com/jiaqi/jmxterm/issues/21) from npiguet/master
- [`d3094c2`](https://github.com/jiaqi/jmxterm/commit/d3094c262e962ff8097511b17322e2d79311fea4) Force the console to flush its buffer, otherwise the watched value is never visible on some environments
- [`fd60bae`](https://github.com/jiaqi/jmxterm/commit/fd60baec8791d0f6c0598b90a7160330a580ada0) Include jmxremote-optional in the uber jar to allow connecting to servers that use jmxmp
- [`921de8b`](https://github.com/jiaqi/jmxterm/commit/921de8b2159d83599920560fc545bec63b94c405) Upgrade parent pom version
- [`e5f4b1e`](https://github.com/jiaqi/jmxterm/commit/e5f4b1ed84d5b7c124f3510a2b6ec73c59f2644f) Merge pull request [#18](https://github.com/jiaqi/jmxterm/issues/18) from Aconex/master
- [`4232fc4`](https://github.com/jiaqi/jmxterm/commit/4232fc4c0b8167aa2920a6848914b058d0102e29) Change the way the RPM is built by defaulting ownership of dirs/files to root, and setting different permissions.
- [`ffc3f4f`](https://github.com/jiaqi/jmxterm/commit/ffc3f4ff27023650c557907f9d213754ab3f5fe0) Fixup parent pom version reference so it can compile.
- [`aed90ee`](https://github.com/jiaqi/jmxterm/commit/aed90ee110c27b7a532003ccd79c71c037636eae) Merge pull request [#23](https://github.com/jiaqi/jmxterm/issues/23) from henri-tremblay/jdk789support
- [`6f88d18`](https://github.com/jiaqi/jmxterm/commit/6f88d18f63508a67215d9e58b95e24f4bc8b5c0d) JConsoleClassLoaderFactoryTest now pass on a JDK above 6
- [`80be2ff`](https://github.com/jiaqi/jmxterm/commit/80be2ff3a8b0b953acff79d9ac2a9f95cd3e6641) Merge pull request [#27](https://github.com/jiaqi/jmxterm/issues/27) from jpoittevin/pom_group
- [`3123572`](https://github.com/jiaqi/jmxterm/commit/312357222c2c835c9059274e61518767e78e9db5) Explicitly set groupId in pom.xml
- [`56a8bec`](https://github.com/jiaqi/jmxterm/commit/56a8bec7777ea019a862176297fd5beb4989c881) Fix deb build
- [`3ad758d`](https://github.com/jiaqi/jmxterm/commit/3ad758d828949d005fe8d5488c5be053140e0a24) Merge pull request [#29](https://github.com/jiaqi/jmxterm/issues/29) from strator-dev/master
- [`9d22f2a`](https://github.com/jiaqi/jmxterm/commit/9d22f2afc2391753a2b1e826df9c9ea3feed6905) Add Travis
- [`4e2a635`](https://github.com/jiaqi/jmxterm/commit/4e2a6353576194c7789be53bcfa7a68b40e9c2f6) * should not need a '-e' for specific operation description (-o).
- [`7a56133`](https://github.com/jiaqi/jmxterm/commit/7a56133fee82c702d5a75d9c72fd6a9cecfcbd3e) Addition of detailed information on operations :
- [`3d39145`](https://github.com/jiaqi/jmxterm/commit/3d39145e25b31d1306e9094598b7ccec413cff56) Merge pull request [#33](https://github.com/jiaqi/jmxterm/issues/33) from nutfox/nestedattributesupport
- [`8834e1e`](https://github.com/jiaqi/jmxterm/commit/8834e1e9d99b6dad390ea17a01b78e1f4eab9ff3) Added support for one level of nested attribute loading for CompositeDataStructures.
- [`0097424`](https://github.com/jiaqi/jmxterm/commit/009742419a04f84f04001f4a0b4100d8bcde3884) Merge pull request [#32](https://github.com/jiaqi/jmxterm/issues/32) from nutfox/preserveoutputfile
- [`66a099e`](https://github.com/jiaqi/jmxterm/commit/66a099ee4c41acfeaa5511278b35e983eab6fd59) Added option to preserve the output text file.
- [`1058b78`](https://github.com/jiaqi/jmxterm/commit/1058b789cc78cd70867d4cbc36db0d77fe8f9208) Merge pull request [#30](https://github.com/jiaqi/jmxterm/issues/30) from nutfox/getcommandforcsv
- [`f0e43a4`](https://github.com/jiaqi/jmxterm/commit/f0e43a4879a88327f10e43cf6791f5177940d587) Added support for CSV output by allowing delimiter to be set and making line break configurable.
- [`e77060d`](https://github.com/jiaqi/jmxterm/commit/e77060dbb0c225582c53c37c089370fc7dac884f) Merge pull request [#25](https://github.com/jiaqi/jmxterm/issues/25) from kandreev/master
- [`86610f0`](https://github.com/jiaqi/jmxterm/commit/86610f0571a8734aeed3eb0a40d36334d7367c09) Adds params list to run command message
- [`022019a`](https://github.com/jiaqi/jmxterm/commit/022019aa47efd67c5d9e92ee3f445d2581771a27) Consider the case where number of given parameters and actual parameters doesn't match
- [`378e25c`](https://github.com/jiaqi/jmxterm/commit/378e25c757a36d3fe3704e9ea1c48f1c77ef9288) Merge pull request [#24](https://github.com/jiaqi/jmxterm/issues/24) from kongslund/method-overloading
- [`70bf75f`](https://github.com/jiaqi/jmxterm/commit/70bf75fe405d1d929754705ab44ab271f6dc0153) Extended run command with support for selecting which overloaded method to invoke by specifying the desired parameter types. Addresses issue #11.
- [`7ad6d15`](https://github.com/jiaqi/jmxterm/commit/7ad6d15b42286bc0492aeab99e1df4c58e13577d) Remove a silly verbose level string list
- [`c01317e`](https://github.com/jiaqi/jmxterm/commit/c01317e0b8dbc2ad6e943f6d8b0879074208e17e) Add test to verify case where domain name contains slash
- [`06625ce`](https://github.com/jiaqi/jmxterm/commit/06625ce34746d05b7e1b002665926b673bbfb567) Flush console output
- [`b8ff7e1`](https://github.com/jiaqi/jmxterm/commit/b8ff7e10838ef061f264c083b9f8bf0e728cf534) Change parent to java parent 0.6
- [`843cecb`](https://github.com/jiaqi/jmxterm/commit/843cecbd1d56f06463306903cd8b4ba52ec408be) Merge pull request [#16](https://github.com/jiaqi/jmxterm/issues/16) from skillz/feature-deb
- [`e96464f`](https://github.com/jiaqi/jmxterm/commit/e96464f448bd3a12d61ed80f035bbc6557d24e81) Remove depends since some folks use openjdk and some use oracle
- [`d66cde1`](https://github.com/jiaqi/jmxterm/commit/d66cde17c407ebe2f1e314d86d59d6af413395d5) Licese is the correct element to use
- [`d049f6e`](https://github.com/jiaqi/jmxterm/commit/d049f6e86dc0313f277663d3dd1ff9ab0159802f) Configure jdeb to create a deb package
- [`3f3086d`](https://github.com/jiaqi/jmxterm/commit/3f3086daa8165d853e224843cf09d653f56a1e8d) Upgrade JCli to resolve dependency confliction to JLine, mentioned in https://github.com/jiaqi/jmxterm/issues/10
- [`15cba7e`](https://github.com/jiaqi/jmxterm/commit/15cba7e2887f52c73afc886c34de82af323cb300) Set correct distribution bucket name
- [`25b8c49`](https://github.com/jiaqi/jmxterm/commit/25b8c495b87da9df9c45d2efa8dd28f4012b17c6) Replace SNAPSHOT version of parent pom with released version
- [`f50fa42`](https://github.com/jiaqi/jmxterm/commit/f50fa424b450f87f1cabe7f8e6b7b85f56c65e66) Rename remote server id
- [`56880c0`](https://github.com/jiaqi/jmxterm/commit/56880c0837f3c9781b98d330c4f25ff744a1c69f) Merge janvolck and resolve conflict
- [`6e69e2e`](https://github.com/jiaqi/jmxterm/commit/6e69e2eb8feb04d0e6bbeed753412cd5150da4b7) Undo change where collection was not initialised by default
- [`e0acf5b`](https://github.com/jiaqi/jmxterm/commit/e0acf5bbe45de6eadb794a27e9b92c67982ea18f) Added unit tests for subscribe and unsubscribe
- [`304d47a`](https://github.com/jiaqi/jmxterm/commit/304d47a439692f0366abc8f97cd78799354b5c39) Add SubscribeCommand which can be used to subscribe to notifications
- [`d077f4a`](https://github.com/jiaqi/jmxterm/commit/d077f4aa696f35ecbd915401f56d919864bf258b) Add shutdown hook for saving history to history file
- [`3e8a93e`](https://github.com/jiaqi/jmxterm/commit/3e8a93e18de9a6a2799c57c91ee6acac1ca9a2da) Fix argument handling for set command
- [`9424d6e`](https://github.com/jiaqi/jmxterm/commit/9424d6e0ec252955622b976093858755a704fc77) Upgrade jline to latest and introduce persistent history in ~/.jmxterm_history
- [`816a10b`](https://github.com/jiaqi/jmxterm/commit/816a10b6dc0dda436260dbd9760bba89cd10444f) Ignore eclipse warning in pom.xml
- [`4b6452d`](https://github.com/jiaqi/jmxterm/commit/4b6452da7f0a59030882e2d6612ed46118146cf7) Adding unit tests to verify issue https://github.com/jiaqi/jmxterm/issues/6
- [`1b34f54`](https://github.com/jiaqi/jmxterm/commit/1b34f54bbf4aa4c8a0b9b6bfeb9add8b5890997d) Change README to point to WIKI page
- [`3990dce`](https://github.com/jiaqi/jmxterm/commit/3990dceae05c9c0f647077607721915d0396d5ee) Jvms command for Mac OS X fixed
- [`f13afa7`](https://github.com/jiaqi/jmxterm/commit/f13afa7b54a26bea4239254da4bf2bf1695ffc1d) Merge changes
- [`f4f2f80`](https://github.com/jiaqi/jmxterm/commit/f4f2f80b79f2e03808e27ff65ad1c452f6a2eefd) Change caff dependency to dev version
- [`d078874`](https://github.com/jiaqi/jmxterm/commit/d078874474414b5e8fbbb2fd8b0d5f4da48e5c55) Use release version of jcli and caff
- [`fc25083`](https://github.com/jiaqi/jmxterm/commit/fc250836c0356796df54ef14c45a3585b6e313ec) Replace deprecated pom.version
- [`fe260a2`](https://github.com/jiaqi/jmxterm/commit/fe260a25f2ef5a13ac0514b11fa60163be9d1d3f) Parameterize command type
- [`e557494`](https://github.com/jiaqi/jmxterm/commit/e557494f7be9c55e430f73fd8f55ac51eb0e19b4) Fix some compiling error
- [`74c881c`](https://github.com/jiaqi/jmxterm/commit/74c881c6096bc056b24625909af8d0e61c4f596a) Solve bug https://bugs.launchpad.net/jmxterm/+bug/803829
- [`45a7a4e`](https://github.com/jiaqi/jmxterm/commit/45a7a4eed1b5c26f76650246fd8a96c36f60801c) Allows # in bean name
- [`e4509c2`](https://github.com/jiaqi/jmxterm/commit/e4509c21a5830f32da29ce33f2e794f9e72fac2e) Use simplified pom.xml
- [`b4b3a7e`](https://github.com/jiaqi/jmxterm/commit/b4b3a7e0b816c9fb824d89ffa6cada8de8598ad0) Fix site generation
- [`ed23e4f`](https://github.com/jiaqi/jmxterm/commit/ed23e4f9646b3c930f7a5a6e5df9b3dba8c2217b) Fix broken site generation under M3 and change site location to Amazon
- [`e1b01fb`](https://github.com/jiaqi/jmxterm/commit/e1b01fb6de1e076ad3f3c856e6f91b249a2da3d9) Copying files from cyclopsgroup to jmxterm repository
- [`00358b0`](https://github.com/jiaqi/jmxterm/commit/00358b0d086440e6ccfba8a94e788953fd53355a) First commit

<!-- generated by git-cliff -->
