# Columba — HF build (stable delivery keys)

A build of [Columba](https://github.com/torlando-tech/columba) for stations operating Reticulum over
HF, where a station's LXMF delivery key must stay the same so that others can encrypt to the key it
published.

Upstream Columba is unchanged in every other respect. This is not a competing app and not a general-purpose
fork — it is one setting, for one situation.

## The problem this solves

Reticulum ratchets rotate a destination's key and advertise the new one. Peers then encrypt to the
ratchet instead of the published key. For ordinary messaging that is a privacy feature and you want
it on.

For an HF station that publishes its delivery key — in a callsign registry, a directory page, or a
frequency listing — it breaks the whole point: the station can no longer decrypt what people send to
the key it told them to use.

There is no way to turn ratchets off in a released Columba build, and an already-advertised ratchet
cannot be recalled, because peers cache it for about 30 days. So this has to be decided when an
identity is created.

## What this build adds

One option, off by default, behind an **Advanced** disclosure when creating a new identity:

> **Turn off ratchets** — useful for regulated radio segments. This compromises the identity's
> security going forward. Only available for new IDs.

Tick it, and that identity announces without a ratchet. Every other identity in the app behaves
exactly as upstream.

## Install

Grab the APK from the releases page. It is arm64 and a debug build, so Android will warn you about
installing from outside the store — that warning is normal for sideloaded builds.

**Create a new identity and tick the box.** An existing identity cannot be repaired: if it has
already announced, peers have cached its ratchet and will keep using it.

## Verify it worked

If you run Reticulum on a computer, the announce is the ground truth:

```
sqlite3 ~/.reticulum/storage/known_destinations.db \
  "select count(*) from ratchets where destination_hash = X'<your lxmf.delivery hash>'"
```

`0` means the station is not ratcheting. `1` means it is, and peers will encrypt to that ratchet
rather than your published key.

## Status and honesty

This build works for the purpose above, tested on one station. It is a patch of a few dozen lines,
not a rewrite, and it is not something to rely on for anything else.

I am an amateur radio operator, not an Android developer. The change was made with an AI assistant.
I can tell you what I observed on my own station; I cannot speak to its quality more broadly. Read
the patch if that matters to you — it is small.

If upstream ever adds this setting, use upstream. I would rather this build became unnecessary.

## Which upstream version

Built from `torlando-tech/columba` at `f88b006`. Rebased when upstream changes in ways that affect
the patch; otherwise it does not move.

The native (Kotlin) backend is not patched in this build — only the Python backend. That is the
default for release builds, and it works.

## Credits and licence

All credit to [torlando-tech](https://github.com/torlando-tech) for Columba, and to
[markqvist](https://github.com/markqvist) for Reticulum, LXMF and LXST. Same licence as upstream.
